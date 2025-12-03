package learning.courseservice.repository;

import learning.courseservice.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, String> {
    Page<Course>findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Course> findByLevel(String level, Pageable pageable);
    Page<Course> findAll(Pageable pageable);
}
