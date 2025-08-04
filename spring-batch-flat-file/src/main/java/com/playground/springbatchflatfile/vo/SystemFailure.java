package com.playground.springbatchflatfile.vo;

import lombok.Data;

@Data
public class SystemFailure {
    private String errorId;
    private String errorDateTime;
    private String severity;
    private Integer processId;
    private String errorMessage;
}
