package com.cleverdev.oldsystem.controller;

import com.cleverdev.oldsystem.dto.ClientProfileDTO;
import com.cleverdev.oldsystem.service.ClientProfileService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
class ClientProfileController {
    private final ClientProfileService clientProfileService;
    @PostMapping("/clients")
    public List<ClientProfileDTO> getClients(){
        return clientProfileService.getAllClientProfiles();
    }
    @PostMapping("/clients/import-sql")
    public String getSQLImportForClients() {
        return clientProfileService.getSQLImportForClients();
    }

}
