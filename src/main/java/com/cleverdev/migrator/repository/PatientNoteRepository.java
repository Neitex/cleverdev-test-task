package com.cleverdev.migrator.repository;

import com.cleverdev.migrator.model.PatientNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientNoteRepository
        extends JpaRepository<PatientNote, Long>, CustomPatientNoteRepository {
}
