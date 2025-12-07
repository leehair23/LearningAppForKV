package learning.submissionservices.repository;

import learning.submissionservices.entity.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ISubmissionRepository extends MongoRepository<Submission, String> {
    List<Submission> findByProblemId(String problemId);

    long countByStatus(String success);
}
