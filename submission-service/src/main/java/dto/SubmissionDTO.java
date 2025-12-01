package dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SubmissionDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest{
        private String problemId;
        private String language;
        private String sourceCode;
        private String mode;
        private String stdin;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private String id;
        private String status;
        private String output;
        private Long timeExec;
        private Long memoryUsage;
        private String createdAt;
    }
    @Data
    public static class WorkerCallBack{
        private String status;
        private String output;
        private Integer exitCode;
        private Long time;
        private Long memory;
    }
}
