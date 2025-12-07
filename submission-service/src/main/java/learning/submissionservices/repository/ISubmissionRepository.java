package repository;

import com.smartcode.runtimeservice.entity.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ISubmissionRepository extends MongoRepository<Submission, String> {

}
