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

    private String getKey(String contestId){
        return contestId == null ? "leaderboard:global" : "leaderboard:contest:" + contestId;
    }


    public void updateScore(String username, double score, String contestId){
        redisTemplate.opsForZSet().add(getKey(contestId), username, score);
    }
    public void incrementScore(String username, double scoreToAdd, String contestId) {
        redisTemplate.opsForZSet().incrementScore(getKey(contestId), username, scoreToAdd);
    }
    public Set<ZSetOperations.TypedTuple<Object>> getTopUsers(int limit, String contestId) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(getKey(contestId), 0, limit - 1);
    }

    public long getRank(String username){
        Long rank = redisTemplate.opsForZSet().reverseRank(GLOBAL_LB_KEY, username);
        return (rank != null) ? rank + 1 : null;
    }
}
