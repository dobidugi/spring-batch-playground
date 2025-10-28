package com.playground.flatfilewriter.config;

import com.playground.flatfilewriter.vo.DeathNote;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.RecordFieldExtractor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;


// .\gradlew bootRun --args="--spring.batch.job.name=processTerminatorJob terminatorId=TEST,java.lang.String targetCount=5,java.lang.Long" 으로 작업 실행시키기


@Configuration
public class DeathNoteWriteJobConfig {

    @Bean
    public Job deathNoteWriteJob(
            JobRepository jobRepository,
            Step deathNoteWriteStep
    ) {
        return new JobBuilder("deathNoteWriteJob", jobRepository)
                .start(deathNoteWriteStep)
                .build();
    }

    @Bean
    public Step deathNoteWriteStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ListItemReader<DeathNote> deathNoteListReader,
            FlatFileItemWriter<DeathNote> deathNoteWriter2
    ) {
        return new StepBuilder("deathNoteWriteStep", jobRepository)
                .<DeathNote, DeathNote>chunk(10, transactionManager)
                .reader(deathNoteListReader)
                .writer(deathNoteWriter2)
                .build();
    }

    @Bean
    public ListItemReader<DeathNote> deathNoteListReader() {
        List<DeathNote> victims = List.of(
                new DeathNote(
                        "KILL-001",
                        "김배치",
                        "2024-01-25",
                        "CPU 과부하"),
                new DeathNote(
                        "KILL-002",
                        "사불링",
                        "2024-01-26",
                        "JVM 스택오버플로우"),
                new DeathNote(
                        "KILL-003",
                        "박탐묘",
                        "2024-01-27",
                        "힙 메모리 고갈")
        );

        return new ListItemReader<>(victims);
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<DeathNote> deathNoteWriter2(
            @Value("#{jobParameters['outputDir']}") String outputDir) {
        return new FlatFileItemWriterBuilder<DeathNote>()
                .name("deathNoteWriter2")
                .resource(new FileSystemResource(outputDir + "/death_note_report.txt"))
                .formatted()
                .format("처형 ID: %s | 처형일자: %s | 피해자: %s | 사인: %s")
                .sourceType(DeathNote.class)
                .names("victimId", "executionDate", "victimName", "causeOfDeath")
                .headerCallback(writer -> writer.write("================= 처형 기록부 ================="))
                .footerCallback(writer -> writer.write("================= 처형 완료 =================="))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<DeathNote> deathNoteWriter(
            @Value("#{jobParameters['outputDir']}") String outputDir) {
        return new FlatFileItemWriterBuilder<DeathNote>()
                .name("deathNoteWriter")
                .resource(new FileSystemResource(outputDir + "/death_notes2.csv"))
                .delimited()
                .delimiter(",")
                .sourceType(DeathNote.class) // 도메인 객체가 Record가 아니라면 생략해도됨
                .fieldExtractor(fieldExtractor())
//                .names("victimId", "victimName", "executionDate", "causeOfDeath") // BeanWrapperFieldExtractor가 객체의 Getter를 호출하기위함이며 순서대로 호출함
                .headerCallback(writer -> writer.write("처형ID,피해자명,처형일자,사인"))
                .build();
    }

    public RecordFieldExtractor<DeathNote> fieldExtractor() {
        RecordFieldExtractor<DeathNote> fieldExtractor = new RecordFieldExtractor<>(DeathNote.class);
        fieldExtractor.setNames("victimId", "executionDate", "causeOfDeath");
        return fieldExtractor;
    }


}