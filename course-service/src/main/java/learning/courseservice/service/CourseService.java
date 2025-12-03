package learning.courseservice.service;

import learning.courseservice.entity.Chapter;
import learning.courseservice.entity.Course;
import learning.courseservice.entity.Lesson;
import learning.courseservice.entity.LessonSummary;
import learning.courseservice.repository.CourseRepository;
import learning.courseservice.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;


    //public stuff
    public List<Course> getAllCourses(){
        return courseRepository.findAll();
    }
    public Course getCourseDetail(String courseId){
        return courseRepository.findById(courseId).orElseThrow(()-> new RuntimeException("course not found"));
    }
    public Lesson getLessonDetail(String lessonId){
        return lessonRepository.findById(lessonId).orElseThrow(() -> new RuntimeException("lesson not found"));
    }

    //admin stuff (CRUD)
    //CRUD course
    public Course createCourse(Course course){
        if (course.getChapters() == null) course.setChapters(new ArrayList<>());
        return courseRepository.save(course);
    }
    public Course addChapter(String courseId, String chapterTitle) {
        Course course = getCourseDetail(courseId);
        Chapter chapter = new Chapter();
        chapter.setId(UUID.randomUUID().toString());
        chapter.setTitle(chapterTitle);
        chapter.setLessons(new ArrayList<>());

        course.getChapters().add(chapter);
        return courseRepository.save(course);
    }
    public Course updateCourse(String id, Course updateData){
        Course course = getCourseDetail(id);

        if (updateData.getTitle() != null) course.setTitle(updateData.getTitle());
        if (updateData.getDescription() != null) course.setDescription(updateData.getDescription());
        if (updateData.getThumbnail() != null) course.setThumbnail(updateData.getThumbnail());
        if (updateData.getLevel() != null) course.setLevel(updateData.getLevel());
        if (updateData.getPrice() != null) course.setPrice(updateData.getPrice());
        if (updateData.getIsPublished() != null) course.setIsPublished(updateData.getIsPublished());

        course.setUpdatedAt(Instant.now());
        return courseRepository.save(course);
    }
    @Transactional
    public void deleteCourse(String id){
        if(!courseRepository.existsById(id)){
            throw new RuntimeException("Course not found");
        }
        lessonRepository.deleteByCourseId(id);
        courseRepository.deleteById(id);
    }
    public Page<Course> searchCourses(String keyword, String level, Pageable pageable){
        if(keyword != null && !keyword.isEmpty()){
            return courseRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        }
        else if(level != null && !level.isEmpty()){
            return courseRepository.findByLevel(level, pageable);
        }
        else {
            return courseRepository.findAll(pageable);
        }
    }

    //CRUD lesson
    @Transactional
    public Lesson addLesson(String courseId, String chapterId, Lesson lesson) {
        Course course = getCourseDetail(courseId);
        Chapter targetChapter = course.getChapters().stream()
                .filter(c -> c.getId().equals(chapterId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        lesson.setCourseId(courseId);
        lesson.setChapterId(chapterId);
        Lesson savedLesson = lessonRepository.save(lesson);

        LessonSummary summary = new LessonSummary();
        summary.setId(savedLesson.getId());
        summary.setTitle(savedLesson.getTitle());
        summary.setType(savedLesson.getType());

        targetChapter.getLessons().add(summary);
        courseRepository.save(course);

        return savedLesson;
    }
    @Transactional
    public Lesson updateLesson(String lessonId, Lesson updateData){
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        boolean needSyncCourse = false;
        if(updateData.getTitle() != null && !updateData.getTitle().equals(lesson.getTitle())){
            lesson.setTitle(updateData.getTitle());
            needSyncCourse = true;
        }
        if (updateData.getType() != null && !updateData.getType().equals(lesson.getType())) {
            lesson.setType(updateData.getType());
            needSyncCourse = true;
        }
        if (updateData.getVideoUrl() != null) lesson.setVideoUrl(updateData.getVideoUrl());
        if (updateData.getContentMd() != null) lesson.setContentMd(updateData.getContentMd());

        if (updateData.getLanguage() != null) lesson.setLanguage(updateData.getLanguage());
        if (updateData.getStarterCode() != null) lesson.setStarterCode(updateData.getStarterCode());
        if (updateData.getExpectedOutput() != null) lesson.setExpectedOutput(updateData.getExpectedOutput());
        if (updateData.getSolutionCode() != null) lesson.setSolutionCode(updateData.getSolutionCode());

        Lesson savedLesson = lessonRepository.save(lesson);

        if (needSyncCourse){
            syncLessonWithCourse(lesson.getCourseId(), lesson.getChapterId(), savedLesson);
        }
        return savedLesson;
    }
    private void syncLessonWithCourse(String courseId, String chapterId, Lesson updatedLesson){
        Course course = getCourseDetail(courseId);
        if(course.getChapters() != null){
            for(Chapter chapter : course.getChapters()){
                if(chapter.getId().equals(chapterId)){
                    for(LessonSummary summary : chapter.getLessons()){
                        if(summary.getId().equals(updatedLesson.getId())){
                            summary.setTitle(updatedLesson.getTitle());
                            summary.setType(updatedLesson.getType());
                            break;
                        }
                    }
                }
            }
            courseRepository.save(course);
        }
    }
    public void deleteLesson(String courseId, String lessonId){
        lessonRepository.findById(lessonId);

        Course course = getCourseDetail(courseId);
        if(course.getChapters() != null){
            for (Chapter chapter : course.getChapters()){
                chapter.getLessons().removeIf(l -> l.getId().equals(lessonId));
            }
            courseRepository.save(course);
        }
    }

}
