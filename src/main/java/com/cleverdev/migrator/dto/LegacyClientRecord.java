package com.cleverdev.migrator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LegacyClientRecord(String agency, String guid, String firstName, String lastName,
                                 String status,
                                 @JsonProperty("dob") String dateOfBirth, String createdDateTime) {
}
