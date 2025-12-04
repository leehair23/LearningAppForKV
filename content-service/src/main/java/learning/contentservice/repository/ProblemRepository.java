package learning.contentservice.repository;

import learning.contentservice.entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProblemRepository extends MongoRepository<Problem, String> , CustomProblemRepository {
    Page<Problem> findByTitleContainingIgnoreCaseAndDifficulty(String title, String difficulty, Pageable pageable);
    Page<Problem> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Problem> findByDifficulty(String difficulty, Pageable pageable);
}
