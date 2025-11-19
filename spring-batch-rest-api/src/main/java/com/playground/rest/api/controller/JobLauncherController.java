package com.playground.rest.api.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/jobs")
public class JobLauncherController {
    private final JobRegistry jobRegistry;

    private final JobLauncher jobLauncher;

    private final JobExplorer jobExplorer;

//    private final ApplicationContext applicationContext;

    @PostMapping("/{jobName}/start")
    public ResponseEntity<String> launchJob(
            @PathVariable String jobName) throws Exception {
        Job job;
        try {
            // 해당 jobName으로 job이 Spring Bean에 등록이 되어있는지 확인하는 과정
            job = jobRegistry.getJob(jobName);
        } catch (NoSuchJobException e) {
            return ResponseEntity.badRequest().body("Unknown job name: " + jobName);
        }

        // jobExplorer를 이용하여 이전 실행이력 바탕으로 JobParameters를 생성
        JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(job)
                .toJobParameters();

        JobExecution execution = jobLauncher.run(job, jobParameters);
        return ResponseEntity.ok("Job launched with ID: " + execution.getId());
    }
}