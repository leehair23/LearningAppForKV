package learning.courseservice.repository;

import learning.courseservice.entity.Progress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends MongoRepository<Progress, String> {
    Optional<Progress> findByUserIdAndLessonId(String userId, String lessonId);

    long countByUserIdAndCourseIdAndIsCompletedTrue(String userId, String courseId);
    List<Progress> findByUserIdAndCourseId(String userId, String courseId);
}
