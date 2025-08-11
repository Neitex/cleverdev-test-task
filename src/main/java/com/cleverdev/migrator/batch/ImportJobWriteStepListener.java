package com.cleverdev.migrator.batch;

import com.cleverdev.migrator.dto.LegacyNotePatientMapping;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.Chunk;

@Slf4j
public class ImportJobWriteStepListener
        implements ItemWriteListener<List<LegacyNotePatientMapping>> {
    @Override public void afterWrite(Chunk<? extends List<LegacyNotePatientMapping>> items) {
        if (items.getItems().isEmpty()) {
            return;
        }
        if (StepSynchronizationManager.getContext() == null){
            log.warn("No context returned from StepSynchronizationManager");
            return;
        }
        Map<String, Object> stepContext =
                StepSynchronizationManager.getContext().getJobExecutionContext();
        long notesCount = items.getItems().stream().mapToLong(List::size).sum();
        if (notesCount > 0) {
            AtomicLong counter =
                    (AtomicLong) stepContext.get(ImportBatchConfiguration.IMPORT_NOTES_COUNT_KEY);
            if (counter == null) {
                log.warn("No notes count found in step context");
                return;
            }
            long newCount = counter.addAndGet(notesCount);
            log.debug("Processed {} legacy notes in this chunk, total processed: {}",
                    notesCount, newCount);
        }
    }
}
