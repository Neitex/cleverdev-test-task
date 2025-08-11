package com.cleverdev.oldsystem.repository;

import com.cleverdev.oldsystem.model.ClientNote;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;

@Controller
public interface ClientNoteRepository extends JpaRepository<ClientNote, String>, CustomClientNoteRepository {
    List<ClientNote> findClientNotesByClientGuidAndClientAgencyAndDatetimeBetween(
            String client_guid, String client_agency, LocalDateTime start,
            LocalDateTime end);
}
