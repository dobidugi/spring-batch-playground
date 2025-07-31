package com.playground.springbatchjoblistener.batch;

import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;

@Component
public class AnnotationBasedJobExecutionListener {

    @BeforeJob
    public void beforeJob(org.springframework.batch.core.JobExecution jobExecution) {
        System.out.println("Job is about to start: " + jobExecution.getJobInstance().getJobName());
    }

    @AfterJob
    public void afterJob(org.springframework.batch.core.JobExecution jobExecution) {
        System.out.println("Job has completed: " + jobExecution.getJobInstance().getJobName());
        if (jobExecution.getStatus().isUnsuccessful()) {
            System.out.println("Job failed with status: " + jobExecution.getStatus());
        } else {
            System.out.println("Job completed successfully.");
        }
    }
}
