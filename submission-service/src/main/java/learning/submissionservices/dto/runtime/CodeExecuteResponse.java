package learning.submissionservices.dto.runtime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CodeExecuteResponse {
    private String output;
    private String status;   // DONE, TIMEOUT, ERROR
    private int exitCode;
    private long timeExec;
    private long memoryUsage;
}
