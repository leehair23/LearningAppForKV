package learning.userservices.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class LearderboardService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String GLOBAL_LB_KEY = "leaderboard:global";

    public void updateScore(String username, double totalScore){
        redisTemplate.opsForZSet().add(GLOBAL_LB_KEY, username, totalScore);
    }
    public void incrementScore(String username, double totalScore){
        redisTemplate.opsForZSet().add(GLOBAL_LB_KEY, username, totalScore);
    }
    public Set<ZSetOperations.TypedTuple<Object>> getTopUser(int limit){
        return redisTemplate.opsForZSet().reverseRangeWithScores(GLOBAL_LB_KEY, 0, limit - 1);
    }

    public long getRank(String username){
        Long rank = redisTemplate.opsForZSet().reverseRank(GLOBAL_LB_KEY, username);
        return (rank != null) ? rank + 1 : null;
    }
}
