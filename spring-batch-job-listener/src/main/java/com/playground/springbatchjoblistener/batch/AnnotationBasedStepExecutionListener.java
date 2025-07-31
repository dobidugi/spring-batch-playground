package com.playground.springbatchjoblistener.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;

@Component
public class AnnotationBasedStepExecutionListener {

     @BeforeStep
     public void beforeStep(StepExecution stepExecution) {
         System.out.println("Before Step: " + stepExecution.getStepName());
     }

     @AfterStep
     public ExitStatus afterStep(StepExecution stepExecution) {
         System.out.println("After Step: " + stepExecution.getStepName());
         return stepExecution.getExitStatus();
     }

}
