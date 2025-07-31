package com.playground.springbatchjoblistener.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnnotationBasedJobExecutionListener {

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Job is about to start: " + jobExecution.getJobInstance().getJobName());

        // Job Execution Context에 데이터를 추가
        jobExecution.getExecutionContext().put("contextData", getContextData());
    }

    public List<String> getContextData() {
        return List.of("Context Data 1", "Context Data 2");
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        System.out.println("Job has completed: " + jobExecution.getJobInstance().getJobName());

        // Step에서 Execution Context에 추가한 데이터르 가져와 출력
        System.out.println("Final Data: " + jobExecution.getExecutionContext().get("finalData"));
        if (jobExecution.getStatus().isUnsuccessful()) {
            System.out.println("Job failed with status: " + jobExecution.getStatus());
        } else {
            System.out.println("Job completed successfully.");
        }
    }
}
