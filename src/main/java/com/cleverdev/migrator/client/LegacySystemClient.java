package com.cleverdev.migrator.client;

import com.cleverdev.migrator.dto.LegacyClientRecord;
import com.cleverdev.migrator.dto.LegacyNoteRecord;
import com.cleverdev.migrator.dto.LegacyNoteRequestRecord;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "legacy-system-client", url = "${migrator.legacy-system.url}")
public interface LegacySystemClient {
    @RequestMapping(method = RequestMethod.POST, path = "/clients")
    List<LegacyClientRecord> getClients();

    @RequestMapping(method = RequestMethod.POST, path = "/notes")
    List<LegacyNoteRecord> getNotesForClient(@RequestBody LegacyNoteRequestRecord record);
}
