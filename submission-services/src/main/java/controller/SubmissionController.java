package controller;


import dto.SubmissionDTO;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import repository.SubmissionService;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {
    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody SubmissionDTO.CreateRequest request){
        return ResponseEntity.status(201).body(submissionService.create(request));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id){
        return ResponseEntity.ok(submissionService.getById(id));
    }
    @PatchMapping("/{id}/result")
    public ResponseEntity<Void>callback(
            @PathVariable String id,
            @RequestBody SubmissionDTO.WorkerCallBack callbackData
    ){
        submissionService.processCallback(id, callbackData);
        return ResponseEntity.ok().build();
    }
}
