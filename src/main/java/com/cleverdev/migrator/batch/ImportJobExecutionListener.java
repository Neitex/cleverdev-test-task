package com.cleverdev.migrator.batch;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

@Slf4j public class ImportJobExecutionListener implements JobExecutionListener {
    @Override public void beforeJob(JobExecution jobExecution) {
        log.info("Starting import job: {} (execution id: {})",
                jobExecution.getJobInstance().getJobName(), jobExecution.getId());
        ExecutionContext context = jobExecution.getExecutionContext();
        synchronized (context) {
            if (context.get(ImportBatchConfiguration.IMPORT_NOTES_COUNT_KEY) == null) {
                context.put(ImportBatchConfiguration.IMPORT_NOTES_COUNT_KEY, new AtomicLong(0L));
            }
        }
    }

    @Override public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus().isUnsuccessful()) {
            log.error("Import job {} (execution id {}) failed with status: {}",
                    jobExecution.getJobInstance().getJobName(), jobExecution.getId(),
                    jobExecution.getStatus());
        } else {
            ExecutionContext context = jobExecution.getExecutionContext();
            AtomicLong notesCount =
                    (AtomicLong) context.get(ImportBatchConfiguration.IMPORT_NOTES_COUNT_KEY);
            if (notesCount == null) {
                notesCount = new AtomicLong(0L);
                log.warn("No notes count found in execution context, defaulting to 0");
            }
            log.info(
                    "Import job {} (execution id {}) completed successfully with status: {}, total run time: {}, notes processed: {}",
                    jobExecution.getJobInstance().getJobName(), jobExecution.getId(),
                    jobExecution.getStatus(),
                    (java.time.Duration.between(Objects.requireNonNull(jobExecution.getStartTime()),
                            jobExecution.getEndTime()).toMillis() / 1000.0) + " s", notesCount);
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
