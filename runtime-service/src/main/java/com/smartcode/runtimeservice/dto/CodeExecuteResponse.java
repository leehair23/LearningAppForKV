package com.smartcode.runtimeservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CodeExecuteResponse {
    private String output; // stdout + stderr
    private String status; // DONE, TIMEOUT, ERROR
    private int exitCode;
    private long timeExec;    // ms
    private long memoryUsage; // KB
}
