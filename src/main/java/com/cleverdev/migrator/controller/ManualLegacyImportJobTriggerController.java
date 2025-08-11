package com.cleverdev.migrator.controller;

import com.cleverdev.migrator.service.LegacyNotesImportJobControlService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ManualLegacyImportJobTriggerController {
    private final LegacyNotesImportJobControlService legacyNotesImportJobControlService;

    @PostMapping("/trigger-legacy-import")
    public String triggerLegacyImport() {
        if (legacyNotesImportJobControlService.importLegacyNotes()){
            return "ok";
        } else {
            return "error";
        }
    }
}
