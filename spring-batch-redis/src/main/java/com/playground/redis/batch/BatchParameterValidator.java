package com.playground.redis.batch;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;

@Component
public class BatchParameterValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        if (parameters.getString("terminatorId") == null || parameters.getString("terminatorId").isEmpty()) {
            throw new JobParametersInvalidException("terminatorId 파라미터는 필수입니다.");
        }

        if( parameters.getLong("targetCount") == null) {
            throw new JobParametersInvalidException("targetCount is required.");
        }
        if (parameters.getLong("targetCount") <= 0) {
            throw new JobParametersInvalidException("targetCount 파라미터는 양의 정수여야 합니다.");
        }

    }
}
