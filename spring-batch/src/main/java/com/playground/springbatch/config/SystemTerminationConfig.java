package com.playground.springbatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.atomic.AtomicInteger;

// ./gradlew bootRun --args='--spring.batch.job.name=systemTerminationSimulationJob' 으로 작업 실행시키기

@Configuration
public class SystemTerminationConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private AtomicInteger processesKilled = new AtomicInteger(0);
    private final int TERMINATION_TARGET = 5;

    public SystemTerminationConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job systemTerminationSimulationJob() {
        System.out.println("test");
        return new JobBuilder("systemTerminationSimulationJob", jobRepository)
                .start(firstStep())
                .next(secondStep())
                .next(thridStep())
                .next(finalStep())
                .build();
    }

    @Bean
    public Step firstStep() {
        return new StepBuilder("firstStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("System Termination 시작!");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step secondStep() {
        return new StepBuilder("secondStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("프로세스  " + TERMINATION_TARGET + "개 실행하기");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step thridStep() {
        return new StepBuilder("thridStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    int terminated = processesKilled.incrementAndGet();
                    System.out.println("Process (현재 " + terminated + "/" + TERMINATION_TARGET + ")");
                    if (terminated < TERMINATION_TARGET) {
                        return RepeatStatus.CONTINUABLE;
                    } else {
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }

    @Bean
    public Step finalStep() {
        return new StepBuilder("finalStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Progress " + TERMINATION_TARGET + "개 처리 완료");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}