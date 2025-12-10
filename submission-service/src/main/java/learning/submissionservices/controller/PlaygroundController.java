package learning.submissionservices.controller;

import learning.submissionservices.dto.runtime.CodeExecuteRequest;
import learning.submissionservices.dto.runtime.CodeExecuteResponse;
import learning.submissionservices.utils.RuntimeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/playground")
@RequiredArgsConstructor
public class PlaygroundController {
    private final RuntimeClient runtimeClient;

    @PostMapping("/execute")
    public ResponseEntity<CodeExecuteResponse> execute(@RequestBody CodeExecuteRequest req){
        if (req.getId() == null || req.getId().isEmpty()) {
            req.setId(UUID.randomUUID().toString());
        }
        if (req.getTimeLimit() == null) req.setTimeLimit(5.0); // 5 giây
        if (req.getMemoryLimit() == null) req.setMemoryLimit(128000L); // 128MB
        try {
            log.info("Requesting Runtime execution for Job ID: {}", req.getId());
            CodeExecuteResponse result = runtimeClient.execute(req);
            log.info("Runtime execution successful for Job ID: {}", req.getId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to execute code in Playground for Job ID: " + req.getId(), e);
            // Trả về lỗi chi tiết để debug
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
