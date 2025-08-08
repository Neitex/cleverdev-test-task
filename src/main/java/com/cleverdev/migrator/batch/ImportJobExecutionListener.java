package com.cleverdev.migrator.batch;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;

@Slf4j public class ImportJobExecutionListener implements JobExecutionListener {
    @Override public void beforeJob(JobExecution jobExecution) {
        log.info("Starting import job: {} (execution id: {})",
                jobExecution.getJobInstance().getJobName(), jobExecution.getId());
    }

    @Override public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus().isUnsuccessful()) {
            log.error("Import job {} (execution id {}) failed with status: {}",
                    jobExecution.getJobInstance().getJobName(), jobExecution.getId(),
                    jobExecution.getStatus());
        } else {
            log.info(
                    "Import job {} (execution id {}) completed successfully with status: {}, total run time: {}",
                    jobExecution.getJobInstance().getJobName(), jobExecution.getId(),
                    jobExecution.getStatus(),
                    (java.time.Duration.between(Objects.requireNonNull(jobExecution.getStartTime()),
                            jobExecution.getEndTime()).toMillis() / 1000.0) + " s");
            for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
                log.info(
                        "Step {} of job {} completed with status: {}, read patients: {}, written patients: {}, skipped: {}, commit count: {}",
                        stepExecution.getStepName(), jobExecution.getJobInstance().getJobName(),
                        stepExecution.getStatus(), stepExecution.getReadCount(),
                        stepExecution.getWriteCount(), stepExecution.getSkipCount(),
                        stepExecution.getCommitCount());
            }
        }
    }
}
