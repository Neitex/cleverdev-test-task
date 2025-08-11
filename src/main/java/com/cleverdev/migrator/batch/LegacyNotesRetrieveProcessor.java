package com.cleverdev.migrator.batch;

import com.cleverdev.migrator.client.LegacySystemClient;
import com.cleverdev.migrator.dto.LegacyNotePatientMapping;
import com.cleverdev.migrator.dto.LegacyNoteRecord;
import com.cleverdev.migrator.dto.LegacyNoteRequestRecord;
import com.cleverdev.migrator.model.PatientProfile;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@RequiredArgsConstructor @Slf4j public class LegacyNotesRetrieveProcessor
        implements ItemProcessor<PatientProfile, List<LegacyNotePatientMapping>> {
    private final LegacySystemClient legacySystemClient;
    private final LegacySystemAgencyCache agencyCache;

    @Override public List<LegacyNotePatientMapping> process(PatientProfile patient) {
        log.debug("Processing patient: {}", patient.getId());
        List<String> guids = Stream.of(patient.getOldClientGuid().split(","))
                .filter(s -> !s.isBlank())
                .map(String::trim)
                .toList();
        LinkedList<LegacyNoteRecord> legacyNotes = new LinkedList<>();
        for (String guid : guids) {
            log.trace("Processing guid {} for patient {}", guid, patient.getId());
            String agencyName = agencyCache.getAgencyNameByClientGUID(guid).orElse(null);
            if (agencyName == null) {
                log.warn("Agency name not found for GUID {} in patient {}", guid, patient.getId());
                continue;
            }
            log.trace("Processing patient {} for client {}", agencyName, patient.getId());
            List<LegacyNoteRecord> notes = legacySystemClient.getNotesForClient(
                    new LegacyNoteRequestRecord(agencyName, "1970-01-01", "2100-01-01", guid));
            log.trace("Received {} notes for patient {}", notes.size(), patient.getId());
            legacyNotes.addAll(notes);
        }
        return legacyNotes.stream()
                .map(note -> new LegacyNotePatientMapping(patient, note))
                .collect(Collectors.toList());
    }
}
