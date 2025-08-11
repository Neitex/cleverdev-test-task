package com.cleverdev.oldsystem.dto;

/*
   @Id
    private String guid;

    private String agency;
    private String firstName;
    private String lastName;
    private String status;
    private String dob;
    private LocalDateTime createdDateTime;
 */

import com.cleverdev.oldsystem.model.ClientProfile;
import java.time.format.DateTimeFormatter;

public record ClientProfileDTO(String guid, String agency, String firstName, String lastName,
                               String status, String dob, String createdDateTime) {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static ClientProfileDTO fromModel(ClientProfile clientProfile) {
        return new ClientProfileDTO(
                clientProfile.getGuid(),
                clientProfile.getAgency(),
                clientProfile.getFirstName(),
                clientProfile.getLastName(),
                clientProfile.getStatus(),
                clientProfile.getDob(),
                clientProfile.getCreatedDateTime().format(formatter)
        );
    }
}
