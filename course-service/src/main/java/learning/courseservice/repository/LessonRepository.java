package learning.courseservice.repository;

import learning.courseservice.entity.Lesson;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LessonRepository extends MongoRepository<Lesson, String> {

    void deleteByCourseId(String courseId);
}
