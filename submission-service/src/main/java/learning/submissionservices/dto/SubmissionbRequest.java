package learning.submissionservices.dto;

import lombok.Data;

@Data
public class SubmissionbRequest {
    private String language;
    private String sourceCode;
    private String mode;

    private String problemId;

    private String stdin;
}
