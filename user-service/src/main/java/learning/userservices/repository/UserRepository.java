package learning.userservices.repository;

import learning.userservices.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserProfile, String> {
    Optional<UserProfile> findByUsername(String username);
    @Query("{ '$or': [ { 'username': { '$regex': ?0, '$options': 'i' } }, { 'email': { '$regex': ?0, '$options': 'i' } } ] }")
    Page<UserProfile> searchUsers(String keyword, Pageable pageable);
}
