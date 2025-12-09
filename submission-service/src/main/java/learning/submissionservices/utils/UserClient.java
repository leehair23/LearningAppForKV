package learning.submissionservices.utils;

import learning.submissionservices.dto.ScoreUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserClient {
    @PatchMapping("/users/{userId}/score")
    void updateScore(
            @PathVariable("userId") String userId,
            @RequestBody ScoreUpdateRequest request,
            @RequestHeader(value = "X-Internal-Secret", required = false) String secret
    );
}
