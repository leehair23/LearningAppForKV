package learning.courseservice.controller;

import learning.courseservice.entity.Course;
import learning.courseservice.entity.Lesson;
import learning.courseservice.service.CourseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    //public API
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable String id){
        return ResponseEntity.ok(courseService.getCourseDetail(id));
    }
    @GetMapping("/lessons/{id}")
    public ResponseEntity<Lesson> getLesson(@PathVariable String id){
        return ResponseEntity.ok(courseService.getLessonDetail(id));
    }
    @GetMapping
    public ResponseEntity<Page<Course>> getAllOrSearch(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ){
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        return ResponseEntity.ok(courseService.searchCourses(q, level, pageRequest));
    }

    //admin API

    //Course
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course, @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role){

        return ResponseEntity.ok(courseService.createCourse(course));
    }
    @PostMapping("/{courseId}/chapters")
    public ResponseEntity<Course> addChapter(@PathVariable String courseId, @RequestBody Map<String, String> body, @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role){

        return ResponseEntity.ok(courseService.addChapter(courseId, body.get("title")));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable String id,
            @RequestBody Course course,
            @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role
    ) {

        return ResponseEntity.ok(courseService.updateCourse(id, course));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id, @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role) {

        courseService.deleteCourse(id);
        return ResponseEntity.ok().build();
    }


    //Lesson
    @PostMapping("/{courseId}/chapters/{chapterId}/lessons")
    public ResponseEntity<Lesson> addLesson(
            @PathVariable String courseId,
            @PathVariable String chapterId,
            @RequestBody Lesson lesson,
            @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role
    ) {

        return ResponseEntity.ok(courseService.addLesson(courseId, chapterId, lesson));
    }
    @PutMapping("/lessons/{lessonId}")
    public ResponseEntity<Lesson> updateLesson(
            @PathVariable String lessonId,
            @RequestBody Lesson lesson,
            @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role
    ) {
        return ResponseEntity.ok(courseService.updateLesson(lessonId, lesson));
    }
    @DeleteMapping("/{courseId}/lessons/{lessonId}")
    public ResponseEntity<Void> deleteLesson(
            @PathVariable String courseId,
            @PathVariable String lessonId,
            @RequestHeader(value = "X-Auth-Role", defaultValue = "USER") String role
    ) {
        courseService.deleteLesson(courseId, lessonId);
        return ResponseEntity.ok().build();
    }
}
