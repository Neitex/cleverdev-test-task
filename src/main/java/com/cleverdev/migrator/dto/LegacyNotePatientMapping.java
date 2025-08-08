package com.cleverdev.migrator.dto;

import com.cleverdev.migrator.model.PatientProfile;

public record LegacyNotePatientMapping(PatientProfile patient, LegacyNoteRecord legacyNoteRecord) {
}
