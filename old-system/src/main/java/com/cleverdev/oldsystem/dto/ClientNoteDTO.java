package com.cleverdev.oldsystem.dto;

import com.cleverdev.oldsystem.model.ClientNote;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record ClientNoteDTO(String guid, String clientGuid, String comments, String loggedUser,
                            String datetime, String createdDateTime, String modifiedDateTime) {
    private static final DateTimeFormatter META_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    public static ClientNoteDTO fromModel(ClientNote clientNote) {
        return new ClientNoteDTO(
                clientNote.getGuid(),
                clientNote.getClient().getGuid(),
                clientNote.getComments(),
                clientNote.getLoggedUser(),
                clientNote.getDatetime().atZone(ZoneId.systemDefault()).format(DATE_TIME_FORMATTER),
                clientNote.getCreatedDateTime().format(META_DATE_TIME_FORMATTER),
                clientNote.getModifiedDateTime().format(META_DATE_TIME_FORMATTER)
        );
    }
}
