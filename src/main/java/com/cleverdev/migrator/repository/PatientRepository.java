package com.cleverdev.migrator.repository;

import com.cleverdev.migrator.model.PatientProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PatientRepository extends JpaRepository<PatientProfile, Long> {
    @SuppressWarnings("unused") @Query("select p from PatientProfile p where p.statusId in (200, 210, 230)") // Active statuses
    Page<PatientProfile> findAllActive(Pageable pageable);
}
