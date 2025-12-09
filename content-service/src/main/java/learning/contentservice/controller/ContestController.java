package learning.contentservice.controller;

import learning.contentservice.dto.ContestDTO;
import learning.contentservice.entity.Contest;
import learning.contentservice.service.ContestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/contests")
public class ContestController {
    private final ContestService contestService;
    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }
    @GetMapping
    public ResponseEntity<Page<ContestDTO>> getContests(
            @RequestParam(required = false) String status, // UPCOMING, RUNNING, ENDED, ACTIVE
            @RequestParam(required = false) String difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(contestService.getContests(status, difficulty, pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ContestDTO> getContest(@PathVariable String id){
        return ResponseEntity.ok(contestService.getContestDetail(id));
    }

    @PostMapping("/generate")
    public ResponseEntity<Contest> generateContest(
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role
            ){
        if (!"ADMIN".equals(role)) return ResponseEntity.status(403).build();
        String title = (String) body.get("title");
        int count = (int) body.getOrDefault("count", 5);
        int duration = (int) body.getOrDefault("duration", 120);
        String difficulty = (String) body.get("difficulty");

        return ResponseEntity.ok(contestService.createAutoContest(title, duration, count, difficulty));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Contest> updateContest(
            @PathVariable String id,
            @RequestBody Contest contest,
            @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role
    ) {
        if (!"ADMIN".equals(role)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(contestService.updateContest(id, contest));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContest(@PathVariable String id,
                                              @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role
    ) {
        if (!"ADMIN".equals(role)) return ResponseEntity.status(403).build();
        contestService.deleteContest(id);
        return ResponseEntity.ok().build();
    }

}
