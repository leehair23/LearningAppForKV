package learning.auth.repository;

import learning.auth.entity.PasswordResetToken;
import learning.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user);

    Optional<PasswordResetToken> findByUser(User user);
}
