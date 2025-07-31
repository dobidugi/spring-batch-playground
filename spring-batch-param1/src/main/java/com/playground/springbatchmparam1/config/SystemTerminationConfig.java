package com.playground.springbatchmparam1.config;

import com.playground.springbatchmparam1.batch.BatchParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.converter.JsonJobParametersConverter;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


// .\gradlew bootRun --args="--spring.batch.job.name=processTerminatorJob terminatorId=TEST,java.lang.String targetCount=5,java.lang.Long" 으로 작업 실행시키기


@Configuration
public class SystemTerminationConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public SystemTerminationConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job processTerminatorJob(JobRepository jobRepository, Step terminationStep, BatchParameterValidator batchParameterValidator) {
        return new JobBuilder("processTerminatorJob", jobRepository)
//                .validator(batchParameterValidator) // JobParametersValidator를 사용하여 파라미터 검증
                .validator(new DefaultJobParametersValidator(
                        new String[]{"terminatorId", "targetCount"}, // 필수 파라미터
                        new String[]{} // 선택적 파라미터는 없음
                ))
                .start(terminationStep)
                .build();
    }

    @Bean
    public Step terminationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet terminatorTasklet) {
        return new StepBuilder("terminationStep", jobRepository)
                .tasklet(terminatorTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet terminatorTasklet(
            @Value("#{jobParameters['terminatorId']}") String terminatorId,
            @Value("#{jobParameters['targetCount']}") Integer targetCount
    ) {
        return (contribution, chunkContext) -> {
            System.out.println("ID: " +  terminatorId);
            System.out.println("제거 대상 수: " + targetCount);
            System.out.println(terminatorId);
            System.out.println("프로세스를 종료합니다." +  targetCount);

            for (int i = 1; i <= targetCount; i++) {
                System.out.println("프로세스 " + i + " 종료 완료!");
            }

            System.out.println(" 모든 대상 프로세스가 종료되었습니다.");
            return RepeatStatus.FINISHED;
        };
    }




}