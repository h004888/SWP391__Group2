package com.OLearning.service.lesson;


import com.OLearning.entity.Lesson;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface LessonService {
    List<Lesson> findAllLessons();

    Lesson findLessonById(Long id);

    Lesson saveLesson(Lesson lesson);

    void deleteLesson(Long id);
    List<Lesson> findLessonsByChapterId(Long chapterId);
    void updateContentType(Long lessonId, String contenType);
    void autoFillOrderNumbers(Long chapterId);


    Lesson updateLesson(Long id, Lesson lessonDetails);

    Lesson getCurrentLearningLesson(Long userId, Long courseId);
    int countLessonsInCourse(Long courseId);

    boolean existsById(Long id);
    Optional<Lesson> findById(Long id);

    Lesson getNextLesson(Long userId, Long courseId);
    Lesson findFirstLesson(Long courseId);

    Optional<Lesson> getNextLessonAfterCompleted(Long userId, Long courseId);
}