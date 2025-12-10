package learning.courseservice.controller;

import learning.courseservice.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class ProgressController {
    private final ProgressService progressService;

    @PostMapping("/complete")
    public ResponseEntity<?> completeLesson(
            @RequestBody Map<String, String> body,
            @RequestHeader("X-Auth-User") String username
    ){
        String courseId = body.get("courseId");
        String lessonId = body.get("lessonId");
        progressService.markLessonComplete(username, courseId, lessonId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> getProgress(
            @PathVariable String courseId,
            @RequestHeader("X-Auth-User") String username
    ) {
        return ResponseEntity.ok(progressService.getCourseProgress(username, courseId));
    }
}
