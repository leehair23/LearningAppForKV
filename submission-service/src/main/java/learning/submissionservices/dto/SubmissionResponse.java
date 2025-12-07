package learning.submissionservices.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionResponse {
    private String id;
    private String status;
    private String output;
    private String expectedOutput;
    private Long timeExec;
    private Long memoryUsage;
    private String createdAt;
}
