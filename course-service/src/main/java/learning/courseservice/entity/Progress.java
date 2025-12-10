package learning.courseservice.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("user_lesson_progress")
@CompoundIndex(name = "user_lesson_idx", def = "{'userId': 1, 'lessonId': 1}", unique = true)
public class Progress {
    @Id
    private String id;

    private String userId;
    private String courseId;
    private String lessonId;

    private boolean isCompleted;
    private Instant completedAt;
}
