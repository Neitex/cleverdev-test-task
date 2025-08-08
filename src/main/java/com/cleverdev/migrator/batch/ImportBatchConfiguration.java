package com.cleverdev.migrator.batch;

import com.cleverdev.migrator.client.LegacySystemClient;
import com.cleverdev.migrator.dto.LegacyNotePatientMapping;
import com.cleverdev.migrator.model.PatientProfile;
import com.cleverdev.migrator.repository.CompanyUserRepository;
import com.cleverdev.migrator.repository.PatientNoteRepository;
import com.cleverdev.migrator.repository.PatientRepository;
import java.util.HashMap;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.ResourcelessJobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration @EnableBatchProcessing public class ImportBatchConfiguration {

    @Bean @StepScope protected LegacySystemAgencyCache legacySystemAgencyCache(
            LegacySystemClient legacySystemClient) {
        return new LegacySystemAgencyCache(legacySystemClient);
    }

    @Bean @StepScope
    protected ItemReader<PatientProfile> activePatientsReader(PatientRepository patientRepository)
            throws Exception {
        RepositoryItemReader<PatientProfile> reader = new RepositoryItemReader<>();
        reader.setRepository(patientRepository);
        reader.setMethodName("findAllActive");
        reader.setPageSize(100);
        HashMap<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        reader.setSort(sorts);
        reader.afterPropertiesSet();
        return reader;
    }

    @Bean @StepScope
    protected ItemProcessor<PatientProfile, List<LegacyNotePatientMapping>> legacyNotesRetrieveProcessor(
            LegacySystemClient legacySystemClient, LegacySystemAgencyCache agencyCache) {
        return new LegacyNotesRetrieveProcessor(legacySystemClient, agencyCache);
    }

    @Bean protected ItemWriter<List<LegacyNotePatientMapping>> legacyNotesWriter(
            PatientNoteRepository patientNoteRepository,
            CompanyUserRepository companyUserRepository) {
        return new LegacyNotesWriter(patientNoteRepository, companyUserRepository);
    }

    @Bean protected Step importLegacyNotesStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<PatientProfile> activePatientsReader,
            ItemProcessor<PatientProfile, List<LegacyNotePatientMapping>> legacyNotesRetrieveProcessor,
            ItemWriter<List<LegacyNotePatientMapping>> legacyNotesWriter) {
        final int CHUNK_SIZE = 66; // on average 15 notes per patient, so ~1k notes per page
        return new StepBuilder("importLegacyNotesStep",
                jobRepository).<PatientProfile, List<LegacyNotePatientMapping>>chunk(CHUNK_SIZE,
                        transactionManager)
                .reader(activePatientsReader)
                .processor(legacyNotesRetrieveProcessor)
                .writer(legacyNotesWriter)
                .build();
    }

    @Bean protected ImportJobExecutionListener importJobExecutionListener() {
        return new ImportJobExecutionListener();
    }

    @Bean protected JobRepository jobRepository() {
        return new ResourcelessJobRepository();
    }

    @Bean
    protected Job importLegacyNotesJob(JobRepository jobRepository, Step importLegacyNotesStep,
            ImportJobExecutionListener importJobExecutionListener) {
        return new JobBuilder("importLegacyNotesJob", jobRepository).start(importLegacyNotesStep)
                .listener(importJobExecutionListener)
                .build();
    }

    @Bean protected JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }
}
