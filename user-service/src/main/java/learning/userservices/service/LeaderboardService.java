package learning.userservices.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String GLOBAL_LB_KEY = "leaderboard:global";

    /**
     * Helper: Lấy Key Redis dựa trên Contest ID
     * Nếu contestId null -> Trả về Key Global
     */
    private String getKey(String contestId) {
        return (contestId == null || contestId.isEmpty())
                ? GLOBAL_LB_KEY
                : "leaderboard:contest:" + contestId;
    }

    public void updateScore(String userId, double score, String contestId) {
        redisTemplate.opsForZSet().add(getKey(contestId), userId, score);
    }

    public void incrementScore(String userId, double scoreToAdd, String contestId) {
        redisTemplate.opsForZSet().incrementScore(getKey(contestId), userId, scoreToAdd);
    }

    public Set<ZSetOperations.TypedTuple<Object>> getTopUsers(int limit, String contestId) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(getKey(contestId), 0, limit - 1);
    }

    public Long getRank(String userId, String contestId) {
        Long rank = redisTemplate.opsForZSet().reverseRank(getKey(contestId), userId);

        return (rank != null) ? rank + 1 : null;
    }

    public Double getScore(String userId, String contestId) {
        return redisTemplate.opsForZSet().score(getKey(contestId), userId);
    }
}