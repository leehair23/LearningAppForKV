package learning.contentservice.controller;

import learning.contentservice.dto.ContestDTO;
import learning.contentservice.entity.Contest;
import learning.contentservice.service.ContestService;
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
}
