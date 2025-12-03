package learning.courseservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("lessons")
public class Lesson {
    @Id
    private String id;
    private String courseId;
    private String chapterId;

    private String title;
    private String type;

    private String videoUrl;
    private String contentMd;

    private String language;
    private String starterCode;
    private String expectedOutput;
    private String solutionCode;
}
