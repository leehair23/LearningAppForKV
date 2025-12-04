package learning.contentservice.repository;

import learning.contentservice.entity.Contest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContestRepository extends MongoRepository<Contest, String> {
}
