package com.playground.rest.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/jobs")
public class JobOperatorController {
    private final JobRegistry jobRegistry;

    private final JobExplorer jobExplorer;

    private final JobLauncher jobLauncher;

    private final JobOperator jobOperator;

    @GetMapping("/{jobName}/executions")
    public ResponseEntity<List<String>> getJobExecutions(@PathVariable String jobName) {
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, 10);
        List<String> executionInfo = new ArrayList<>();

        for (JobInstance jobInstance : jobInstances) {
            List<JobExecution> executions = jobExplorer.getJobExecutions(jobInstance);
            for (JobExecution execution : executions) {
                executionInfo.add(String.format("Execution ID: %d, Status: %s",
                        execution.getId(), execution.getStatus()));
            }
        }

        return ResponseEntity.ok(executionInfo);
    }


    @PostMapping("/stop/{executionId}")
    public ResponseEntity<String> stopJob(@PathVariable Long executionId) throws Exception {
        boolean stopped = jobOperator.stop(executionId);
        return ResponseEntity.ok("Stop request for job execution " + executionId +
                (stopped ? " successful" : " failed"));
    }

    @PostMapping("/restart/{executionId}")
    public ResponseEntity<String> restartJob(@PathVariable Long executionId) throws Exception {
        Long newExecutionId = jobOperator.restart(executionId);
        return ResponseEntity.ok("Job restarted with new execution ID: " + newExecutionId);
    }

}
