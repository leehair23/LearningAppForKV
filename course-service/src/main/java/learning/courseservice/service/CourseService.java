package learning.courseservice.service;

import learning.courseservice.entity.Chapter;
import learning.courseservice.entity.Course;
import learning.courseservice.entity.Lesson;
import learning.courseservice.entity.LessonSummary;
import learning.courseservice.repository.CourseRepository;
import learning.courseservice.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    private final MongoTemplate mongoTemplate;


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
    public Course updateChapter(String courseId, String chapterId, String newTitle) {
        Course course = getCourseDetail(courseId);

        if (course.getChapters() != null) {
            for (Chapter chapter : course.getChapters()) {
                if (chapter.getId().equals(chapterId)) {
                    chapter.setTitle(newTitle);
                    break;
                }
            }
            course.setUpdatedAt(java.time.Instant.now());
            return courseRepository.save(course);
        }
        throw new RuntimeException("Chapter not found");
    }
    @Transactional
    public void deleteChapter(String courseId, String chapterId) {
        Course course = getCourseDetail(courseId);

        if (course.getChapters() != null) {
            Chapter targetChapter = course.getChapters().stream()
                    .filter(c -> c.getId().equals(chapterId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Chapter not found"));

            for (LessonSummary summary : targetChapter.getLessons()) {
                lessonRepository.deleteById(summary.getId());
            }

            course.getChapters().remove(targetChapter);

            course.setUpdatedAt(java.time.Instant.now());
            courseRepository.save(course);
        }
    }
    public Course updateCourse(String id, Course updateData){
        Course course = getCourseDetail(id);

        if (updateData.getTitle() != null) course.setTitle(updateData.getTitle());
        if (updateData.getDescription() != null) course.setDescription(updateData.getDescription());
        if (updateData.getThumbnail() != null) course.setThumbnail(updateData.getThumbnail());
        if (updateData.getLevel() != null) course.setLevel(updateData.getLevel());
        if (updateData.getLanguage() != null) course.setLanguage(updateData.getLanguage());
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
    public Page<Course> searchCourses(String keyword, String level, String language, Pageable pageable){
        Query query = new Query();
        if (keyword != null && !keyword.isEmpty()) {
            Criteria nameCriteria = Criteria.where("title").regex(keyword, "i");
            Criteria descCriteria = Criteria.where("description").regex(keyword, "i");
            query.addCriteria(new Criteria().orOperator(nameCriteria, descCriteria));
        }
        if (level != null && !level.isEmpty()) {
            query.addCriteria(Criteria.where("level").regex("^" + level + "$", "i"));
        }
        if (language != null && !language.isEmpty()) {
            query.addCriteria(Criteria.where("language").regex("^" + language + "$", "i"));
        }
        long total = mongoTemplate.count(query, Course.class);
        query.with(pageable);
        List<Course> courses = mongoTemplate.find(query, Course.class);

        return new PageImpl<>(courses, pageable, total);
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
    public int totalLesson(){
        return 1;
    }
}
