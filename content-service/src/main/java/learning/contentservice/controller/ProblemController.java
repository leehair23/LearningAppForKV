package learning.contentservice.controller;

import learning.contentservice.dto.ProblemDTO;
import learning.contentservice.entity.Problem;
import learning.contentservice.service.ProblemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/problems")
public class ProblemController {
    @Value("${app.internal-secret}")
    private String internalSecret;
    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    //user view
    @GetMapping
    public ResponseEntity<Page<ProblemDTO>> getProblems(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(problemService.getProblems(q,difficulty,tags,pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemDTO> getDetails(@PathVariable String id){
        return ResponseEntity.ok(problemService.getProblemForUser(id));
    }

    //internal
    @GetMapping("/{id}/full")
    public ResponseEntity<?> getFullDetail(
            @PathVariable String id,
            @RequestHeader(value = "X-Internal-Secret", defaultValue = "") String secretKey
    ){
        if (!internalSecret.equals(secretKey)) {
            return ResponseEntity.status(403).body("Internal Access Only");
        }
        return ResponseEntity.ok(problemService.getProblemFull(id));
    }
    //admin
    @PostMapping
    public ResponseEntity<Problem> create(@RequestBody Problem problem, @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role){
        if(!"ADMIN".equals(role)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(problemService.createProblem(problem));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Problem> update(@PathVariable String id, @RequestBody Problem problem, @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role){
        if(!"ADMIN".equals(role)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(problemService.updateProblem(id, problem));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role){
        if(!"ADMIN".equals(role)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        problemService.deleteProblem(id);
        return ResponseEntity.ok().build();
    }
}
