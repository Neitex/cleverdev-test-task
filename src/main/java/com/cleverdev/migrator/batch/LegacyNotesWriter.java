package com.cleverdev.migrator.batch;

import com.cleverdev.migrator.dto.LegacyNotePatientMapping;
import com.cleverdev.migrator.model.CompanyUser;
import com.cleverdev.migrator.model.PatientNote;
import com.cleverdev.migrator.repository.CompanyUserRepository;
import com.cleverdev.migrator.repository.PatientNoteRepository;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j @RequiredArgsConstructor public class LegacyNotesWriter
        implements ItemWriter<List<LegacyNotePatientMapping>> {
    private final PatientNoteRepository patientNoteRepository;
    private final CompanyUserRepository companyUserRepository;

    private static boolean validate(LegacyNotePatientMapping mapping) {
        return mapping != null
                && mapping.legacyNoteRecord() != null
                && mapping.legacyNoteRecord().loggedUser() != null
                && !mapping.legacyNoteRecord().loggedUser().isBlank()
                && mapping.patient() != null
                && mapping.legacyNoteRecord().createdDateTime() != null
                && mapping.legacyNoteRecord().modifiedDateTime() != null;
    }

    @Override public void write(Chunk<? extends List<LegacyNotePatientMapping>> items) {
        List<LegacyNotePatientMapping> notes =
                items.getItems().stream().flatMap(Collection::stream).toList();
        if (notes.isEmpty()) {
            return;
        }
        Set<String> userLogins = notes.stream()
                .map(i -> i.legacyNoteRecord().loggedUser().toLowerCase())
                .collect(Collectors.toSet());
        Map<String, CompanyUser> userMap = companyUserRepository.findAllByLoginIn(userLogins)
                .stream()
                .collect(Collectors.toMap(CompanyUser::getLogin, i -> i));
        List<String> missingLogins =
                userLogins.stream().filter(login -> !userMap.containsKey(login)).toList();
        if (!missingLogins.isEmpty()) {
            missingLogins.forEach(companyUserRepository::createByLoginIfNotExists);
            companyUserRepository.findAllByLoginIn(missingLogins)
                    .forEach(user -> userMap.put(user.getLogin(), user));
        }
        List<PatientNote> patientNotes = notes.stream().map(item -> {
            String login = item.legacyNoteRecord().loggedUser().toLowerCase();
            CompanyUser user = userMap.get(login);
            if (user == null) {
                throw new IllegalStateException("User not found after creation: " + login);
            }
            if (!validate(item)) {
                log.warn("Invalid legacy note mapping: {}", item);
                return null; // Skip invalid mappings
            }
            return getPatientNote(item, user);
        }).filter(Objects::nonNull).toList();
        patientNoteRepository.saveOrUpdateIfNewer(patientNotes);
    }

    private static PatientNote getPatientNote(LegacyNotePatientMapping item, CompanyUser user) {
        PatientNote patientNote = new PatientNote();
        patientNote.setPatient(item.patient());
        patientNote.setNote(item.legacyNoteRecord().comments());
        patientNote.setLegacyGuid(item.legacyNoteRecord().guid());
        patientNote.setCreatedByUser(user);
        patientNote.setCreatedDateTime(item.legacyNoteRecord().parseCreatedDateTime());
        patientNote.setLastModifiedDateTime(item.legacyNoteRecord().parseModifiedDateTime());
        patientNote.setLastModifiedByUser(user);
        return patientNote;
    }
}
