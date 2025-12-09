package learning.contentservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document("contests")
public class Contest {
    @Id
    private String id;
    private String title;
    private String description;

    private String difficulty;

    private Instant startTime;
    private Instant endTime;

    private List<String> problemIds;
    private Instant createdAt = Instant.now();
    private Instant updatedAt;
}
