package com.playground.springbatchflatfile.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor    // 필수!
public class LogEntry {
    private String severity;
    private String threadNum;
    private String cpuUsage;
    private String message;
}