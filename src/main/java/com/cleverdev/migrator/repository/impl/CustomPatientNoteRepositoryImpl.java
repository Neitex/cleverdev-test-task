package com.cleverdev.migrator.repository.impl;

import com.cleverdev.migrator.model.PatientNote;
import com.cleverdev.migrator.repository.CustomPatientNoteRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Slf4j @Repository
@RequiredArgsConstructor
public class CustomPatientNoteRepositoryImpl implements CustomPatientNoteRepository {
    private final EntityManager entityManager;

    @Override @Transactional public void saveOrUpdateIfNewer(List<PatientNote> notes) {
        if (notes == null || notes.isEmpty()) {
            return;
        }
        log.trace("Saving {} note(s) to DB", notes.size());
        String sql = """
                    INSERT INTO patient_note (
                        legacy_guid, created_date_time, last_modified_date_time,
                        created_by_user_id, last_modified_by_user_id, note, patient_id
                    ) VALUES (?,?, ?, ?, ?, ?, ?)
                    ON CONFLICT (legacy_guid)
                    DO UPDATE SET
                        created_date_time = EXCLUDED.created_date_time,
                        last_modified_date_time = EXCLUDED.last_modified_date_time,
                        last_modified_by_user_id = EXCLUDED.last_modified_by_user_id,
                        note = EXCLUDED.note,
                        patient_id = EXCLUDED.patient_id
                    WHERE patient_note.last_modified_date_time < EXCLUDED.last_modified_date_time
                """;
        entityManager.unwrap(Session.class).doWork(connection -> {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                for (PatientNote patientNote : notes) {
                    if (patientNote.getCreatedByUser() == null
                            || patientNote.getLastModifiedByUser() == null
                            || patientNote.getPatient() == null) {
                        log.warn("Skipping note with null user references: id={} guid={}",
                                patientNote.getId(), patientNote.getLegacyGuid());
                        continue;
                    }
                    ps.setString(1, patientNote.getLegacyGuid());
                    ps.setTimestamp(2, Timestamp.valueOf(patientNote.getCreatedDateTime()));
                    ps.setTimestamp(3, Timestamp.valueOf(patientNote.getLastModifiedDateTime()));
                    ps.setLong(4, patientNote.getCreatedByUser().getId());
                    ps.setLong(5, patientNote.getLastModifiedByUser().getId());
                    ps.setString(6, patientNote.getNote());
                    ps.setLong(7, patientNote.getPatient().getId());
                    ps.addBatch();
                }
                int[] execs = ps.executeBatch();
                log.debug("Executed batch for {} notes, executed queries: {}", notes.size(), execs.length);
            }
        });
    }
}
