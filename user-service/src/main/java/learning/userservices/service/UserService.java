package learning.userservices.service;

import learning.userservices.DTO.Response;
import learning.userservices.DTO.ScoreUpdate;
import learning.userservices.DTO.UpdateRequest;
import learning.userservices.entity.UserProfile;
import learning.userservices.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Response getOrCreateProfile(String username, String email) {
        UserProfile user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
             user = UserProfile.builder()
                    .username(username)
                    .email(email != null ? email : "")
                    .fullName(username)
                    .bio("Coding enthusiast")
                    .avtUrl("https://ui-avatars.com/api/?name=" + username)
                    .score(0.0)
                    .rank("ROOKIE")
                    .solveCount(0)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            user = userRepository.save(user);
        } else {
            boolean needUpdate = false;

            if (email != null && !email.isEmpty()) {
                if (user.getEmail() == null || user.getEmail().isEmpty()) {
                    user.setEmail(email);
                    needUpdate = true;
                }
            }

            if (needUpdate) {
                user.setUpdatedAt(Instant.now());
                user = userRepository.save(user);
            }
        }

        return mapToDTO(user);
    }
    public Response getPublicProfile(String username){
        UserProfile user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return mapToDTO(user);
    }
    public Response updateProfile(String username, UpdateRequest req){
        UserProfile user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        if(req.getFullName() != null) user.setFullName(req.getFullName());
        if(req.getBio()!=null) user.setBio(req.getBio());
        if(req.getAvatarUrl() != null) user.setAvtUrl(req.getAvatarUrl());

        UserProfile savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }
    public void updateScore(String username, ScoreUpdate req){
        Optional<UserProfile> userOpt = userRepository.findByUsername(username);
        if(userOpt.isPresent()){
            UserProfile user = userOpt.get();

            if(req.getScoreToAdd() != null){
                user.setScore(user.getScore() + req.getScoreToAdd());
            }
            if (Boolean.TRUE.equals(req.getIncrementSolved())){
                user.setSolveCount(user.getSolveCount() + 1);
            }
            updateRank(user);
            userRepository.save(user);
        }
    }
    private void updateRank(UserProfile user){
        double s = user.getScore();
        if (s >= 1000) user.setRank("GOLD");
        else if (s >= 500) user.setRank("SILVER");
        else if (s >= 100) user.setRank("BRONZE");
        else user.setRank("ROOKIE");
    }
    private Response mapToDTO(UserProfile user){
        return Response.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .bio(user.getBio())
                .avatarUrl(user.getAvtUrl())
                .score(user.getScore())
                .rank(user.getRank())
                .solvedCount(user.getSolveCount())
                .createdAt(user.getCreatedAt().toString())

                .build();
    }
}
