package com.cleverdev.oldsystem.repository.impl;

import com.cleverdev.oldsystem.repository.ClientNoteRepository;
import com.cleverdev.oldsystem.repository.CustomClientNoteRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CustomClientNoteRepositoryImpl implements CustomClientNoteRepository {
    private final EntityManager entityManager;
    private final Random random = new SecureRandom();

    @Override @Transactional public void updateAllNotes() {
        String selectQuery = """
                SELECT guid FROM client_note;
                """;
        String updateQuery = """
                UPDATE client_note
                SET comments = ?, modified_date_time = ?
                WHERE guid = ?;
                """;
        var notes = entityManager.createNativeQuery(selectQuery).getResultList();
        entityManager.unwrap(Session.class).doWork(connection -> {
            PreparedStatement st =  connection.prepareStatement(updateQuery);
            for (Object note : notes) {
                String guid = (String) note;
                String comment = "Updated comment " + random.nextLong();
                st.setString(1, comment);
                st.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                st.setString(3, guid);
                st.addBatch();
            }
            st.executeBatch();
        });
    }
}
