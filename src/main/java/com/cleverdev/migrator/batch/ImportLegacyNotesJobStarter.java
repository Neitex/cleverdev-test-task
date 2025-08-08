package com.cleverdev.migrator.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j @Component @RequiredArgsConstructor public class ImportLegacyNotesJobStarter {
    private final JobLauncher jobLauncher;
    private final Job importLegacyNotesJob;

    @Scheduled(cron = "${migrator.import.run-cron:0 15 1/2 * * *}") public void importLegacyNotes()
            throws Exception {
        log.info("Starting legacy notes import job");
        jobLauncher.run(importLegacyNotesJob, new JobParameters());
    }
}
