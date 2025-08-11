package com.cleverdev.oldsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ClientNotesRequestDTO(@NotBlank String agency, @NotNull LocalDate dateFrom,
                                    @NotNull LocalDate dateTo, @NotBlank String clientGuid) {
}
