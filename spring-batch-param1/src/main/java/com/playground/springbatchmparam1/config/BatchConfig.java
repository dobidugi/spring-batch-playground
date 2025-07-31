package com.playground.springbatchmparam1.config;

import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.converter.JsonJobParametersConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class BatchConfig {

    // JobParametersConverter 빈을 정의하여 JSON 형식의 JobParameters를 사용하도록 설정
    @Primary
    @Bean
    public JobParametersConverter jobParametersConverter() {
        return new JsonJobParametersConverter();
    }
}
