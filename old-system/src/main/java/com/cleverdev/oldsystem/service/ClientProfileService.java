package com.cleverdev.oldsystem.service;

import com.cleverdev.oldsystem.dto.ClientProfileDTO;
import com.cleverdev.oldsystem.model.ClientProfile;
import com.cleverdev.oldsystem.repository.ClientProfileRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientProfileService {
    private final ClientProfileRepository clientProfileRepository;

    public List<ClientProfileDTO> getAllClientProfiles() {
        return clientProfileRepository.findAll().stream().map(ClientProfileDTO::fromModel).toList();
    }

    public String getSQLImportForClients() {
        List<ClientProfile> clientProfiles =
                clientProfileRepository.findAll().stream().limit(999).toList();
        StringBuilder sqlImport = new StringBuilder();
        sqlImport.append(
                "INSERT INTO patient_profile (first_name, last_name, old_client_guid, status_id) VALUES");
        ListUtils.partition(clientProfiles, 3).forEach(clientProfile -> {
            String values = String.format("\n('%s', '%s', '%s', %d),",
                    clientProfile.get(0).getFirstName(),
                    clientProfile.get(0).getLastName(),
                    clientProfile.stream()
                            .map(ClientProfile::getGuid)
                            .reduce("", (a, b) -> a + "," + b).replaceFirst(",", ""),
                    200);
            sqlImport.append(values);
        });
        sqlImport.deleteCharAt(sqlImport.length() - 1);
        sqlImport.append(";");
        return sqlImport.toString();
    }
}
