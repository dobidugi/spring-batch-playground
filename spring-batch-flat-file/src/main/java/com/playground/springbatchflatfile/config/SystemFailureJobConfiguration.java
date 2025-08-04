package com.playground.springbatchflatfile.config;

import com.playground.springbatchflatfile.vo.SystemFailure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.item.file.FlatFileItemReader;


// .\gradlew bootRun --args="--spring.batch.job.name=systemFailureJob fileName=.\failure.csv"
@Slf4j
@RequiredArgsConstructor
@Configuration
public class SystemFailureJobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job jobRegister(
            Step systemFailureStep
    ) {
        return new JobBuilder("systemFailureJob", jobRepository)
                .start(systemFailureStep)
                .build();
    }

    @Bean
    public Step systemFailureStep(
            FlatFileItemReader<SystemFailure> systemFailureItemReader,
            SystemFailureStdoutItemWriter systemFailureStdoutItemWriter
    ) {
        return new StepBuilder("systemFailureStep", jobRepository)
                .<SystemFailure, SystemFailure>chunk(10, transactionManager) // chunkSize: 10
                .reader(systemFailureItemReader)
                .writer(systemFailureStdoutItemWriter)
                .build();


    }

    @Bean
    @StepScope
    public FlatFileItemReader<?> systemFailureItemReader(
            @Value("#{jobParameters['fileName']}") String fileName
    ) {
        return new FlatFileItemReaderBuilder<SystemFailure>()
                .name("systemFailureItemReader")
                .resource(new FileSystemResource(fileName))
                .delimited() // 구분자 기반 파일 읽기 활성화
                .delimiter(",") // 구분자
                .names(
                        "errorId",
                        "errorDateTime",
                        "severity",
                        "processId",
                        "errorMessage"
                )
                .targetType(SystemFailure.class)
                .linesToSkip(1) // 헤더 처리
                .build();
    }


    @Bean
    public SystemFailureStdoutItemWriter systemFailureStdoutItemWriter() {
        return new SystemFailureStdoutItemWriter();
    }

    public static class SystemFailureStdoutItemWriter implements ItemWriter<SystemFailure> {
        @Override
        public void write(Chunk<? extends SystemFailure> chunk) throws Exception {
            for (SystemFailure failure : chunk) {
                log.info("Processing system failure: {}", failure);
            }
        }
    }


}
