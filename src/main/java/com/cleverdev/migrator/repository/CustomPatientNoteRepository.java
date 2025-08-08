package com.cleverdev.migrator.repository;

import com.cleverdev.migrator.model.PatientNote;
import java.util.List;

public interface CustomPatientNoteRepository {

    /**
     * Saves new PatientNotes or updates existing ones if they are newer than the current ones in the database.
     *
     * @param notes the list of PatientNotes to save or update
     */
    void saveOrUpdateIfNewer(List<PatientNote> notes);
}
