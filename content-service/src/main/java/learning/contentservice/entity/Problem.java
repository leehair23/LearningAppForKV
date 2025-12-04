package learning.contentservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("problems")
public class Problem {
    @Id
    private String id;

    private String title;
    private String description;
    private String difficulty;  // EASY, MEDIUM, HARD
    private List<String> tags;

    // Resource Limits
    private Double timeLimit = 1.0; // Seconds
    private Long memoryLimit = 256000L; // KB

    private Map<String, String> templates;
    private Map<String, String> driverCode;

    private List<TestCase> testCases;

    private Instant createdAt =  Instant.now();
    private Instant updatedAt = Instant.now();
}
