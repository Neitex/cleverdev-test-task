package com.cleverdev.migrator.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service @AllArgsConstructor @Slf4j public class LegacyNotesImportJobControlService {
    private final JobLauncher jobLauncher;
    private final Job importLegacyNotesJob;

    @Scheduled(cron = "${migrator.import.run-cron:0 15 1/2 * * *}") public boolean importLegacyNotes() {
        JobParameters jobParameters = new JobParametersBuilder().addLocalDateTime("timestamp",
                java.time.LocalDateTime.now()).toJobParameters();
        log.info("Starting legacy notes import job with parameters {}", jobParameters);
        try {
            jobLauncher.run(importLegacyNotesJob, jobParameters);
            return true;
        } catch (Exception e) {
            log.error("Failed to start legacy notes import job", e);
            return false;
        }
    }
}
