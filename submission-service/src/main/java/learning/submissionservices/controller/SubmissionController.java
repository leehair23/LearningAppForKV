package learning.submissionservices.controller;

import learning.submissionservices.dto.SubmissionResponse;
import learning.submissionservices.dto.SubmissionbRequest;
import learning.submissionservices.dto.runtime.CodeExecuteResponse;
import learning.submissionservices.repository.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService  submissionService;
    @PostMapping
    public ResponseEntity<SubmissionResponse> create(
            @RequestBody SubmissionbRequest request,
            @RequestHeader("X-Auth-UserId") String userId
    ){
        return ResponseEntity.ok(submissionService.create(userId, request));
    }
    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponse> get(@PathVariable String id){
        return ResponseEntity.ok(submissionService.getById(id));
    }
    @PatchMapping("/{id}/result")
    public ResponseEntity<Void> callback(
            @PathVariable String id,
            @RequestBody CodeExecuteResponse response
    ){
        submissionService.processCallback(id, response);
        return ResponseEntity.ok().build();
    }
}
