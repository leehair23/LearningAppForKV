package learning.submissionservices.utils;

import learning.submissionservices.dto.content.ProblemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "content-service")
public interface ContentClient {
    @GetMapping("problems/{id}/full")
    ProblemDTO getProblem(
            @PathVariable("id") String id,
            @RequestHeader("X-Internal-Secret") String secret
    );
}
