package learning.courseservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document("courses")
public class Course {
    @Id
    private String id;
    private String title;
    private String description;
    private String thumbnail;
    private String level;
    private String language;
    private Double price;

    private List<Chapter> chapters;

    private Boolean isPublished = false;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
}

