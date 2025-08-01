package com.playground.springbatchjoblistener.config;

import com.playground.springbatchjoblistener.batch.AnnotationBasedJobExecutionListener;
import com.playground.springbatchjoblistener.batch.AnnotationBasedStepExecutionListener;
import com.playground.springbatchjoblistener.batch.SomethingJobExecutionListener;
import com.playground.springbatchjoblistener.batch.SomethingStepExecutionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;


// .\gradlew bootRun --args="--spring.batch.job.name=somethingJob" 으로 작업 실행시키기

@Configuration
public class JobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SomethingJobExecutionListener somethingJobExecutionListener;
    private final SomethingStepExecutionListener somethingStepExecutionListener;
    private final AnnotationBasedJobExecutionListener annotationBasedJobExecutionListener;
    private final AnnotationBasedStepExecutionListener annotationBasedStepExecutionListener;

    public JobConfiguration(
            JobRepository jobRepository, PlatformTransactionManager transactionManager,
            SomethingJobExecutionListener somethingJobExecutionListener, SomethingStepExecutionListener somethingStepExecutionListener,
            AnnotationBasedStepExecutionListener annotationBasedStepExecutionListener, AnnotationBasedJobExecutionListener annotationBasedJobExecutionListener
    ) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.somethingJobExecutionListener = somethingJobExecutionListener;
        this.somethingStepExecutionListener = somethingStepExecutionListener;
        this.annotationBasedJobExecutionListener = annotationBasedJobExecutionListener;
        this.annotationBasedStepExecutionListener = annotationBasedStepExecutionListener;
    }



    @Bean
    public Job Somethingjob(JobRepository jobRepository, Step somethingStep, Step finalStep) {
        return new JobBuilder("somethingJob", jobRepository)
                .start(somethingStep)
                .next(finalStep)
//                .listener(somethingJobExecutionListener)
                .listener(annotationBasedJobExecutionListener) // Annotation 기반 JobExecutionListener 사용
                .build();
    }

    @Bean
    public Step somethingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet somethingTasklet) {
        return new StepBuilder("somethingStep", jobRepository)
                .tasklet(somethingTasklet, transactionManager)
//                .listener(somethingStepExecutionListener)
                .listener(annotationBasedStepExecutionListener) // Annotation 기반 StepExecutionListener 사용
                .build();
    }

    @Bean
    public Step finalStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet finalTasklet) {
        return new StepBuilder("finalStep", jobRepository)
                .tasklet(finalTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet somethingTasklet(
            @Value("#{jobExecutionContext['contextData']}") List<String> contextList
    ) {
        return (contribution, chunkContext) -> {
            // Tasklet logic goes here
            System.out.println("Executing somethingTasklet");
            System.out.println("Context Data: " + contextList);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet finalTasklet() {
        return (contribution, chunkContext) -> {
            // Final Tasklet logic goes here
            System.out.println("Call finalTasklet");
            contribution.getStepExecution().getJobExecution().getExecutionContext().put("finalData", "Final Tasklet Data");
            return RepeatStatus.FINISHED;
        };
    }
}
