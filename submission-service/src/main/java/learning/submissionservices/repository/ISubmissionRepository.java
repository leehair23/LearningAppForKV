package learning.submissionservices.repository;

import learning.submissionservices.entity.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ISubmissionRepository extends MongoRepository<Submission, String> {

}
