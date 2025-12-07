package com.smartcode.runtimeservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CodeExecuteRequest {
    private String id;
    private String language;
    private String sourceCode;
    private String stdin;
    private Double timeLimit;
    private Long memoryLimit;
}
