package com.cleverdev.migrator.batch;

import com.cleverdev.migrator.client.LegacySystemClient;
import com.cleverdev.migrator.dto.LegacyNotePatientMapping;
import com.cleverdev.migrator.dto.LegacyNoteRecord;
import com.cleverdev.migrator.dto.LegacyNoteRequestRecord;
import com.cleverdev.migrator.model.PatientProfile;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LegacyNotesRetrieveProcessorTest {

    private LegacySystemClient legacySystemClient;

    private LegacySystemAgencyCache agencyCache;

    private LegacyNotesRetrieveProcessor processor;

    @BeforeEach void setUp() {
        legacySystemClient = mock(LegacySystemClient.class);
        agencyCache = mock(LegacySystemAgencyCache.class);
        processor = new LegacyNotesRetrieveProcessor(legacySystemClient, agencyCache);
    }

    @DisplayName("process should return mappings for valid patient with multiple GUIDs") @Test
    void processReturnsMappingsForValidPatientWithMultipleGUIDs() {
        PatientProfile patient = new PatientProfile();
        patient.setId(1L);
        patient.setOldClientGuid("guid1,guid2");

        when(agencyCache.getAgencyNameByClientGUID("guid1")).thenReturn(Optional.of("Agency1"));
        when(agencyCache.getAgencyNameByClientGUID("guid2")).thenReturn(Optional.of("Agency2"));
        when(legacySystemClient.getNotesForClient(any(LegacyNoteRequestRecord.class))).thenReturn(
                List.of(new LegacyNoteRecord("Note1", "lnr1", null, "guid1", null, "user1", null),
                        new LegacyNoteRecord("Note2", "lnr2", null, "guid2", null, "user2", null)));

        List<LegacyNotePatientMapping> result = processor.process(patient);

        assertEquals(4, result.size());
    }

    @DisplayName("process should return empty list when patient has no GUIDs") @Test
    void processReturnsEmptyListWhenPatientHasNoGUIDs() {
        PatientProfile patient = new PatientProfile();
        patient.setId(1L);
        patient.setOldClientGuid("");

        List<LegacyNotePatientMapping> result = processor.process(patient);

        assertTrue(result.isEmpty());
    }

    @DisplayName("process should handle GUIDs with extra spaces") @Test
    void processHandlesGUIDsWithExtraSpaces() {
        PatientProfile patient = new PatientProfile();
        patient.setId(1L);
        patient.setOldClientGuid(" guid1 , guid2 ");

        when(agencyCache.getAgencyNameByClientGUID("guid1")).thenReturn(Optional.of("Agency1"));
        when(agencyCache.getAgencyNameByClientGUID("guid2")).thenReturn(Optional.of("Agency2"));
        when(legacySystemClient.getNotesForClient(any(LegacyNoteRequestRecord.class))).thenReturn(
                List.of(new LegacyNoteRecord("Note1", "lnr1", null, "guid1", null, "user1", null)));

        List<LegacyNotePatientMapping> result = processor.process(patient);

        assertEquals(2, result.size());
    }
}
