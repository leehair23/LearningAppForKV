package learning.submissionservices.utils;

import learning.submissionservices.dto.runtime.CodeExecuteRequest;
import learning.submissionservices.dto.runtime.CodeExecuteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "runtime-service")
public interface RuntimeClient {
    @PostMapping("runtime/execute")
    CodeExecuteResponse execute(@RequestBody CodeExecuteRequest req);
}
