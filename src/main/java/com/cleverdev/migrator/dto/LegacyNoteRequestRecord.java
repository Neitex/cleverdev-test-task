package com.cleverdev.migrator.dto;

public record LegacyNoteRequestRecord(String agency, String dateFrom, String dateTo,
                                      String clientGuid) {
}
