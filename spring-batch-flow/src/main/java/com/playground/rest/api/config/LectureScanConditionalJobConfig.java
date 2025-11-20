package com.playground.rest.api.config;

import com.playground.rest.api.vo.ExitStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class LectureScanConditionalJobConfig {

    @Bean
    public Job lectureScanConditionalJob(JobRepository jobRepository,
                                         Step analyzeContentStep,      // State 1
                                         Step publishLectureStep,      // State 2
                                         Step summarizeFailureStep) {  // State 3

        return new JobBuilder("lectureScanConditionalJob", jobRepository)
                // 초기 State 설정
                .start(analyzeContentStep)


                // [Transition 1]
                // IF ExitStatus == "COMPLETED" THEN publishLectureStep State
                .on(ExitStatus.COMPLETED.name())           // 조건: ExitCode가 "COMPLETED"
                .to(publishLectureStep)    // 전이: publishLectureStep으로 이동

                // 다시 analyzeContentStep으로부터 새로운 Transition 정의
                .from(analyzeContentStep)

                // [Transition 2]
                // IF ExitStatus == "FAILED" THEN 다음 summarizeFailureStep State
                .on(ExitStatus.FAILED.name())              // 조건: ExitCode가 "FAILED"
                .to(summarizeFailureStep)  // 전이: summarizeFailureStep으로 이동

                // 모든 Flow 정의 종료
                .end()
                .build();
    }
}
