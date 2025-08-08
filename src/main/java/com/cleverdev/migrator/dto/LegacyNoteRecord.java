package com.cleverdev.migrator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record LegacyNoteRecord(String comments, String guid, String modifiedDateTime,
                               String clientGuid, @JsonProperty("datetime") String dateTime,
                               String loggedUser, String createdDateTime) {
    private final static DateTimeFormatter DATETIME_PATTERN =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LocalDateTime parseModifiedDateTime() {
        return LocalDateTime.parse(modifiedDateTime, DATETIME_PATTERN);
    }

    public LocalDateTime parseCreatedDateTime() {
        return LocalDateTime.parse(createdDateTime, DATETIME_PATTERN);
    }
}
