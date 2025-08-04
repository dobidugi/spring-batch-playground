package com.playground.springbatchflatfile.config;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.item.file.FlatFileItemReader;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SystemFailureJobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job jobRegister() {
        return new JobBuilder("systemFailureJob", jobRepository)
                .start(systemFailureStep())
                .build();
    }

    @Bean
    public Step systemFailureStep() {
//        return new StepBuilder("systemFailureStep", jobRepository);
        return null;

    }

    @Bean
    @StepScope
    public FlatFileItemReader<?> flatfileItemReader(
            @Value(staticConstructor = "#{jobParameters['fileName']}") String fileName
    ) {
        return new FlatfileItemReader();
    }

}
