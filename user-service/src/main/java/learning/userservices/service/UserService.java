package learning.userservices.service;

import learning.userservices.DTO.Response;
import learning.userservices.DTO.ScoreUpdate;
import learning.userservices.DTO.UpdateDTO;
import learning.userservices.DTO.UpdateRequest;
import learning.userservices.client.AuthClient;
import learning.userservices.entity.UserProfile;
import learning.userservices.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthClient authClient;

    public Response getOrCreateProfile(String id, String username, String email) {
        UserProfile user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
             user = UserProfile.builder()
                    .id(id)
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
    public Response getPublicProfile(String id){
        UserProfile user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return mapToDTO(user);
    }
    public Response updateProfile(String id, UpdateRequest req){
        UserProfile user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        if(req.getFullName() != null) user.setFullName(req.getFullName());
        if(req.getBio()!=null) user.setBio(req.getBio());
        if(req.getAvatarUrl() != null) user.setAvtUrl(req.getAvatarUrl());

        UserProfile savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }
    public Page<Response> searchUsers(String keyword, Pageable pageable){
        Page<UserProfile> users;
        if(keyword != null && !keyword.isEmpty()){
            users = userRepository.searchUsers(keyword, pageable);
        }else{
            users = userRepository.findAll(pageable);
        }
        return users.map(this::mapToDTO);
    }
    public void updateScore(String id, ScoreUpdate req){
        Optional<UserProfile> userOpt = userRepository.findById(id);
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
    public void banUser(String id, boolean isActive){
        UserProfile user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        user.setActive(isActive);
        userRepository.save(user);

        try{
            authClient.changeStatus(user.getId(), isActive);
        }catch (Exception e){
            log.info(e.getMessage());
        }
    }
    @Transactional
    public Response updateUser(String id, UpdateDTO req){
        UserProfile user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getBio() != null) user.setBio(req.getBio());
        if (req.getAvatarUrl() != null) user.setAvtUrl(req.getAvatarUrl());
        if (req.getScore() != null) user.setScore(req.getScore());
        if (req.getRank() != null) user.setRank(req.getRank());

        boolean needSyncAuth = false;
        Map<String, String> authUpdates = new HashMap<>();
        if (req.getEmail() != null && !req.getEmail().equals(user.getEmail())) {
            user.setEmail(req.getEmail());
            authUpdates.put("email", req.getEmail());
            needSyncAuth = true;
        }
        if (req.getRole() != null) {
            authUpdates.put("role", req.getRole());
            needSyncAuth = true;
        }
        UserProfile savedUser = userRepository.save(user);
        if (needSyncAuth) {
            try {
                authClient.updateAuthInfo(user.getId(), authUpdates);
            } catch (Exception e) {
                throw new RuntimeException("Failed to sync with Auth Service: " + e.getMessage());
            }
        }
        return mapToDTO(savedUser);
    }
    public void deleteUser(String id){
        UserProfile user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String userId = user.getId();
        userRepository.deleteById(userId);
        try {
            authClient.deleteUser(id);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
    public Map<String, Object> getAnalytics(){
        long totalUsers = userRepository.count();
        long newUsersToday = userRepository.countByCreatedAtAfter(Instant.now().minus(1, ChronoUnit.DAYS));

        return Map.of(
                "totalUsers", totalUsers,
                "newUsersToday", newUsersToday
        );
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
