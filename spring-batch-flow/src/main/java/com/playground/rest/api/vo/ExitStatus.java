package com.playground.rest.api.vo;

public enum ExitStatus {
    COMPLETED("COMPLETED"),
    FAILED("FAILED");

    private final String exitCode;

    private ExitStatus(String exitCode) {
        this.exitCode = exitCode;
    }
}