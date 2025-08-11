package com.cleverdev.oldsystem.controller;

import com.cleverdev.oldsystem.dto.ClientNoteDTO;
import com.cleverdev.oldsystem.dto.ClientNotesRequestDTO;
import com.cleverdev.oldsystem.service.ClientNotesService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController @AllArgsConstructor class ClientNoteController {
    private final ClientNotesService clientNotesService;

    @PostMapping("/notes") public List<ClientNoteDTO> findClientNotes(
            @RequestBody ClientNotesRequestDTO clientNotesRequestDTO) {
        return clientNotesService.findAllByParams(clientNotesRequestDTO);
    }

    @PostMapping("/notes/update") public void updateAllClientNotes() {
        clientNotesService.updateAllClientNotes();
    }
}
