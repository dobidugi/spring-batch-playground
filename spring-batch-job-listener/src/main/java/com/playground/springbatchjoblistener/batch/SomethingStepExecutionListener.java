package com.playground.springbatchjoblistener.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;


@Component
public class SomethingStepExecutionListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("Step is about to start: " + stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("Step has completed: " + stepExecution.getStepName());
        if (stepExecution.getStatus().isUnsuccessful()) {
            System.out.println("Step failed with status: " + stepExecution.getStatus());
        } else {
            System.out.println("Step completed successfully.");
        }
        return stepExecution.getExitStatus();
    }
}
