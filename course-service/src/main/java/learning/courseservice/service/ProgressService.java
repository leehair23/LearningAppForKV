package learning.courseservice.service;

import learning.courseservice.entity.Progress;
import learning.courseservice.repository.ProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProgressService {
    private final ProgressRepository progressRepository;
    private final CourseService courseService;

    public void markLessonComplete(String userId, String courseId, String lessonId){
        Progress progress = progressRepository.findByUserIdAndLessonId(userId, lessonId)
                .orElse(Progress.builder()
                        .userId(userId)
                        .courseId(courseId)
                        .lessonId(lessonId)
                        .build());
        progress.setCompleted(true);
        progress.setCompletedAt(Instant.now());
        progressRepository.save(progress);
    }
    public Map<String, Object> getCourseProgress(String userId, String courseId){
        List<Progress> records = progressRepository.findByUserIdAndCourseId(userId, courseId);
        List<String> completeLessonIds = records.stream()
                .filter(Progress::isCompleted)
                .map(Progress::getLessonId)
                .toList();

        int totalLessons = 10;
        double percent = (double) completeLessonIds.size() / totalLessons * 100;

        return Map.of(
                "courseId", courseId,
                "completedLessonIds", completeLessonIds,
                "progressPercent", Math.round(percent * 10.0) / 10.0,
                "isFinished", percent >= 100
        );
    }
}
