package com.cleverdev.oldsystem.service;

import com.cleverdev.oldsystem.dto.ClientNoteDTO;
import com.cleverdev.oldsystem.dto.ClientNotesRequestDTO;
import com.cleverdev.oldsystem.repository.ClientNoteRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientNotesService {
    private final ClientNoteRepository clientNoteRepository;

    public List<ClientNoteDTO> findAllByParams(ClientNotesRequestDTO requestDTO) {
        return clientNoteRepository
                .findClientNotesByClientGuidAndClientAgencyAndDatetimeBetween(
                        requestDTO.clientGuid(),
                        requestDTO.agency(),
                        requestDTO.dateFrom().atStartOfDay(),
                        requestDTO.dateTo().atTime(23, 59, 59))
                .stream()
                .map(ClientNoteDTO::fromModel)
                .toList();
    }

    public void updateAllClientNotes(){
        clientNoteRepository.updateAllNotes();
    }

}
