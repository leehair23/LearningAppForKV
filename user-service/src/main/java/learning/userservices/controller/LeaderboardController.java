package learning.userservices.controller;

import learning.userservices.DTO.LeaderBoardEntry;
import learning.userservices.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<List<LeaderBoardEntry>> getGlobalLeaderboard(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String contestId
    ){
        Set<ZSetOperations.TypedTuple<Object>> topUsers = leaderboardService.getTopUsers(limit, contestId);

        List<LeaderBoardEntry> response = new ArrayList<>();
        long currentRank = 1;

        if (topUsers != null){
            for (ZSetOperations.TypedTuple<Object> tuple : topUsers){
                String username = (String) tuple.getValue();
                Double score = tuple.getScore();

                response.add(LeaderBoardEntry.builder()
                        .username(username)
                        .score(score)
                        .rank(currentRank++)
                        .build()
                );
            }
        }
        return ResponseEntity.ok(response);
    }
    @GetMapping("rank/{userid}")
    public ResponseEntity<?> getUserRank(@PathVariable String userid, @RequestParam(required = false) String contestId){
        long rank = leaderboardService.getRank(userid,contestId);

        if(rank == 0){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rank);
    }
}
