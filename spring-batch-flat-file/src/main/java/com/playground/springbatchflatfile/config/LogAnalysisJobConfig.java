package com.playground.springbatchflatfile.config;


import com.playground.springbatchflatfile.vo.LogEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.RegexLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;


//  .\gradlew bootRun --args="--spring.batch.job.name=logAnalysisJob fileName=.\failure.log"
@Slf4j
@RequiredArgsConstructor
@Configuration
public class LogAnalysisJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job logAnalysisJob(Step logAnalysisStep) {
        return new JobBuilder("logAnalysisJob", jobRepository)
                .start(logAnalysisStep)
                .build();
    }

    @Bean
    public Step logAnalysisStep(
            FlatFileItemReader<LogEntry> logItemReader,
            ItemWriter<LogEntry> logItemWriter

    ) {
        return new StepBuilder("logAnalysisStep", jobRepository)
                .<LogEntry, LogEntry>chunk(10, transactionManager)
                .reader(logItemReader)
                .writer(logItemWriter)
                .build();
    }


    @Bean
    @StepScope
    public FlatFileItemReader<LogEntry> logItemReader(
            @Value("#{jobParameters['fileName']}") String fileName
    ) {
        RegexLineTokenizer tokenizer = new RegexLineTokenizer();
//        tokenizer.setRegex("\\[\\w+\\]\\[Thread-(\\d+)\\]\\[CPU: \\d+%\\] (.+)");
        tokenizer.setRegex("\\[(\\w+)]\\[Thread-(\\d+)]\\[CPU: (\\d+)%] (.+)");

        tokenizer.setNames("severity", "threadNum", "cpuUsage", "message"); // ★ 필드명 4개

        return new FlatFileItemReaderBuilder<LogEntry>()
                .name("logItemReader")
                .resource(new FileSystemResource(fileName))
                .lineTokenizer(tokenizer)
//                .fieldSetMapper(fieldSet -> new LogEntry(fieldSet.readString(0), fieldSet.readString(1)))
                .targetType(LogEntry.class)
                .build();
    }

    @Bean
    public ItemWriter<LogEntry> logItemWriter() {
        return items -> {
            for (LogEntry logEntry : items) {
                log.info(String.format("THD-%s: %s",
                        logEntry.getThreadNum(), logEntry.getMessage()));
            }
        };
    }
}
