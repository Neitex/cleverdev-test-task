package com.cleverdev.oldsystem.dto;

import java.time.LocalDate;

public record ClientNotesRequestDTO(String agency, LocalDate from, LocalDate to, String clientGuid) {
}
