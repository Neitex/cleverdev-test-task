package com.cleverdev.migrator.batch;

import com.cleverdev.migrator.dto.LegacyNotePatientMapping;
import com.cleverdev.migrator.dto.LegacyNoteRecord;
import com.cleverdev.migrator.model.CompanyUser;
import com.cleverdev.migrator.model.PatientProfile;
import com.cleverdev.migrator.repository.CompanyUserRepository;
import com.cleverdev.migrator.repository.PatientNoteRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class LegacyNotesWriterTest {
    private CompanyUserRepository companyUserRepository;
    private PatientNoteRepository patientNoteRepository;
    private LegacyNotesWriter processor;

    private static CompanyUser getCompanyUser(String login) {
        CompanyUser user = new CompanyUser();
        user.setLogin(login);
        return user;
    }

    @BeforeEach
    void setUp() {
        companyUserRepository = mock(CompanyUserRepository.class);
        patientNoteRepository = mock(PatientNoteRepository.class);
        processor = new LegacyNotesWriter(patientNoteRepository, companyUserRepository);
    }

    @DisplayName("write should save patient notes for valid mappings")
    @Test
    void writeSavesPatientNotesForValidMappings() {
        List<LegacyNotePatientMapping> mappings = List.of(
                new LegacyNotePatientMapping(
                        new PatientProfile(),
                        new LegacyNoteRecord("Note1", "lnr1", "2025-08-08 00:00:00", "guid1", "2025-08-08 00:00:00", "user1", "2025-08-08 00:00:00")),
                new LegacyNotePatientMapping(
                        new PatientProfile(),
                        new LegacyNoteRecord("Note2", "lnr2", "2025-08-08 00:00:00", "guid2", "2025-08-08 00:00:00", "user2", "2025-08-08 00:00:00"))
        );

        when(companyUserRepository.findAllByLoginIn(Set.of("user1", "user2")))
                .thenReturn(List.of(getCompanyUser("user1"), getCompanyUser("user2")));

        processor.write(new Chunk<>(List.of(mappings)));

        verify(patientNoteRepository).saveOrUpdateIfNewer(anyList());
    }

    @DisplayName("write should handle missing users by creating them")
    @Test
    void writeHandlesMissingUsersByCreatingThem() {
        List<LegacyNotePatientMapping> mappings = List.of(
                new LegacyNotePatientMapping(
                        new PatientProfile(),
                        new LegacyNoteRecord("Note1", "lnr1", "2025-08-08 00:00:00", "guid1",
                                "2025-08-08 00:00:00",
                                "user1", "2025-08-08 00:00:00"))
        );
        when(companyUserRepository.findAllByLoginIn(Set.of("user1"))).thenReturn(List.of());
        when(companyUserRepository.findAllByLoginIn(List.of("user1"))).thenReturn(
                List.of(getCompanyUser("user1")));

        processor.write(new Chunk<>(List.of(mappings)));

        verify(companyUserRepository).createByLoginIfNotExists("user1");
        verify(patientNoteRepository).saveOrUpdateIfNewer(anyList());
    }

    @DisplayName("write should throw exception when user creation fails")
    @Test
    void writeThrowsExceptionWhenUserCreationFails() {
        List<LegacyNotePatientMapping> mappings = List.of(
                new LegacyNotePatientMapping(
                        new PatientProfile(),
                        new LegacyNoteRecord("Note1", "lnr1", "2025-08-08 00:00:00", "guid1",
                                "2025-08-08 00:00:00",
                                "user1", "2025-08-08 00:00:00"))
        );

        when(companyUserRepository.findAllByLoginIn(Set.of("user1"))).thenReturn(List.of());
        when(companyUserRepository.createByLoginIfNotExists("user1"))
                .thenReturn(1);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> processor.write(new Chunk<>(List.of(mappings))));
        assertEquals("User not found after creation: user1", exception.getMessage());
    }

    @DisplayName("write should skip processing when no mappings are provided")
    @Test
    void writeSkipsProcessingWhenNoMappingsProvided() {
        processor.write(new Chunk<>(List.of()));

        verifyNoInteractions(companyUserRepository, patientNoteRepository);
    }
}
