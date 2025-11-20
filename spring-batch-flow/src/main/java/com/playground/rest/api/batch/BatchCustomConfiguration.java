package com.playground.rest.api.batch;

import org.springframework.boot.autoconfigure.batch.BatchTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class BatchCustomConfiguration {

    @Bean
    @BatchTaskExecutor
    public TaskExecutor batchTaskExecutor() {
        return new SimpleAsyncTaskExecutor(); // SimpleAsyncTaskExecutor 를 사용하여 batch를 비동기처리
    }
}
