package learning.submissionservices.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
@Data
@Document("submissions")
public class Submission {
    @Id
    private String id;

    @Field("uid")
    private String userId;

    @Field("qid")
    private String problemId; // Null nếu là mode TEST

    @Field("lang")
    private String language;

    @Field("code")
    private String sourceCode;

    @Field("mode")
    private String mode; // "TEST" or "SUBMIT"

    // Kết quả chấm
    @Field("st")
    private String status; // PENDING, ACCEPTED, WRONG_ANSWER...

    @Field("out")
    private String output; // Log kết quả

    @Field("time")
    private Long timeExec; // ms

    @Field("mem")
    private Long memoryUsage; // KB

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

}
