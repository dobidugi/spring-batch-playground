package com.playground.redis.vo;

import com.playground.redis.vo.AttackModels.AttackAnalysisResult;
import com.playground.redis.vo.AttackModels.AttackLog;
import com.playground.redis.vo.AttackModels.AttackType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
@Slf4j
@Component
public class AttackCounter implements JobExecutionListener {
    private static final String UNKNOWN = "Unknown";
    private static final String TIME_SUFFIX = "시";

    // 공격 타입별 카운트
    private final ConcurrentMap<AttackType, Integer> attackTypeCount = new ConcurrentHashMap<>();
    // IP별 공격 횟수
    private final ConcurrentMap<String, Integer> ipAttackCount = new ConcurrentHashMap<>();
    // 💀간대별 그룹핑 (시간 부분만 추출)
    private final ConcurrentMap<Integer, Integer> timeSlotCount = new ConcurrentHashMap<>();
    // 전체 카운트 기록
    private final AtomicInteger totalAttacks = new AtomicInteger(0);

    public void record(AttackLog attackLog) {
        AttackType type = attackLog.getAttackType();
        attackTypeCount.merge(type, 1, Integer::sum);
        ipAttackCount.merge(attackLog.getTargetIp(), 1, Integer::sum);
        timeSlotCount.merge(attackLog.getTimestamp().getHour(), 1, Integer::sum);
        totalAttacks.incrementAndGet();
    }

    public AttackAnalysisResult generateAnalysis() {
        Map<AttackType, String> attackTypePercentage = getAttackTypeCount().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> String.format("%.1f%%", (entry.getValue() * 100.0) / getTotalAttacks())
                ));

        return AttackAnalysisResult.builder()
                .totalAttacks(getTotalAttacks())
                .attackTypeCount(getAttackTypeCount())
                .attackTypePercentage(attackTypePercentage)
                .ipAttackCount(getIpAttackCount())
                .timeSlotCount(getTimeSlotCount())
                .mostDangerousIp(findMostDangerousIp())
                .peakHour(findPeakHour())
                .threatLevel(calculateThreatLevel())
                .build();
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("💀 [KILL-9] 공격 분석 작전 성공! 다음 작전을 위해 데이터 정리 중...");
        reset();
        log.info("💀 [KILL-9] 시스템 초기화 완료. 다음 침입자를 기다린다...");
    }

    private void reset() {
        attackTypeCount.clear();
        ipAttackCount.clear();
        timeSlotCount.clear();
        totalAttacks.set(0);
    }

    private Map<AttackType, Integer> getAttackTypeCount() {
        return new HashMap<>(attackTypeCount);
    }

    private Map<String, Integer> getIpAttackCount() {
        return new HashMap<>(ipAttackCount);
    }

    private Map<String, Integer> getTimeSlotCount() {
        return timeSlotCount.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey() + "시",
                        Map.Entry::getValue
                ));
    }

    public int getTotalAttacks() {
        return totalAttacks.get();
    }

    private String findMostDangerousIp() {
        return ipAttackCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(UNKNOWN);
    }

    private String findPeakHour() {
        return timeSlotCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey() + TIME_SUFFIX)
                .orElse(UNKNOWN);
    }

    private String calculateThreatLevel() {
        int total = totalAttacks.get();
        if (total >= 10) return "CRITICAL";
        if (total >= 5) return "HIGH";
        if (total >= 2) return "MEDIUM";
        return "LOW";
    }
}