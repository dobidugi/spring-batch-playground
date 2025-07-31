package com.playground.springbatchjoblistener.config;

import com.playground.springbatchjoblistener.batch.SomethingJobExecutionListener;
import com.playground.springbatchjoblistener.batch.SomethingStepExecutionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


// .\gradlew bootRun --args="--spring.batch.job.name=somethingJob" 으로 작업 실행시키기

@Configuration
public class JobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SomethingJobExecutionListener somethingJobExecutionListener;
    private final SomethingStepExecutionListener somethingStepExecutionListener;

    public JobConfiguration(
            JobRepository jobRepository, PlatformTransactionManager transactionManager,
            SomethingJobExecutionListener somethingJobExecutionListener, SomethingStepExecutionListener somethingStepExecutionListener
    ) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.somethingJobExecutionListener = somethingJobExecutionListener;
        this.somethingStepExecutionListener = somethingStepExecutionListener;
    }

    @Bean
    public Job Somethingjob(JobRepository jobRepository, Step somethingStep) {
        return new JobBuilder("somethingJob", jobRepository)
                .start(somethingStep)
                .listener(somethingJobExecutionListener)
                .build();
    }

    @Bean
    public Step somethingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet somethingTasklet, Tasklet tasklet) {
        return new StepBuilder("somethingStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .listener(somethingStepExecutionListener)
                .build();
    }

    @Bean
    public Tasklet somethingTasklet() {
        return (contribution, chunkContext) -> {
            // Tasklet logic goes here
            System.out.println("Executing somethingTasklet");
            return RepeatStatus.FINISHED;
        };
    }
}
