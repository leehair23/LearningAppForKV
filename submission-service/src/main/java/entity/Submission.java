package entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document("submissions")
public class Submission {
    @Id
    public String id;

    @Field("uid") public String userId;        // User ID
    @Field("qid") public String problemId;     // Problem ID
    @Field("lang")   public String language;
    @Field("code")   public String sourceCode;
    @Field("in")  public String dataInput;     // Input (chỉ lưu nếu cần thiết)

    // Kết quả
    @Field("st")  public String status;        // PENDING, ACCEPTED...
    @Field("time")   public Long timeExec;        // ms
    @Field("mem")   public Long memoryUsage;     // KB
    @Field("out") public String output;        // Stdout/Stderr (đã cắt ngắn)
    @Field("err") public String error;
    public Instant createdAt = Instant.now();
    public Long updatedAt = System.currentTimeMillis();

    public Submission() {
    }

    public Submission(String id, String userId, String problemId, String language, String sourceCode, String dataInput, String status, Long timeExec, Long memoryUsage, String output, String error, Instant createdAt, Long updatedAt) {
        this.id = id;
        this.userId = userId;
        this.problemId = problemId;
        this.language = language;
        this.sourceCode = sourceCode;
        this.dataInput = dataInput;
        this.status = status;
        this.timeExec = timeExec;
        this.memoryUsage = memoryUsage;
        this.output = output;
        this.error = error;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProblemId() {
        return problemId;
    }

    public void setProblemId(String problemId) {
        this.problemId = problemId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getDataInput() {
        return dataInput;
    }

    public void setDataInput(String dataInput) {
        this.dataInput = dataInput;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimeExec() {
        return timeExec;
    }

    public void setTimeExec(Long timeExec) {
        this.timeExec = timeExec;
    }

    public Long getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Long memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
