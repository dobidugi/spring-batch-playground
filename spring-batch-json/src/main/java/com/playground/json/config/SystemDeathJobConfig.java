package com.playground.json.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.json.vo.DeathNote;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SystemDeathJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ObjectMapper objectMapper;

    public SystemDeathJobConfig(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ObjectMapper objectMapper
    ) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.objectMapper = objectMapper;
    }

    @Bean
    public Job systemDeathJob(Step systemDeathStep) {
        return new JobBuilder("systemDeathJob", jobRepository)
                .start(systemDeathStep)
                .build();
    }

    @Bean
    public Step systemDeathStep(
            JsonItemReader<SystemDeath> systemDeathJsonItemReader,
            JsonFileItemWriter<DeathNote> deathNoteJsonWriter
    ) {
        return new StepBuilder("systemDeathStep", jobRepository)
                .<SystemDeath, DeathNote>chunk(10, transactionManager)
                .reader(systemDeathJsonItemReader)
                .writer(deathNoteJsonWriter)
                .build();
    }

    @Bean
    @StepScope
    public JsonItemReader<SystemDeath> systemDeathJsonItemReader(
            @Value("#{jobParameters['inputFile']}") String inputFile) {
        JacksonJsonObjectReader<SystemDeath> reader = new JacksonJsonObjectReader<>(SystemDeath.class);
        reader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<SystemDeath>()
                .name("systemDeathJsonItemReader")
                .resource(new FileSystemResource(inputFile))
                .jsonObjectReader(reader)
                .build();
    }

    @Bean
    @StepScope
    public JsonFileItemWriter<DeathNote> deathNoteJsonWriter(
            @Value("#{jobParameters['outputDir']}") String outputDir) {
        return new JsonFileItemWriterBuilder<DeathNote>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(new FileSystemResource(outputDir + "/death_notes.json"))
                .name("deathNoteJsonWriter")
                .build();
    }

    public record SystemDeath(String command, int cpu, String status) {}
}
