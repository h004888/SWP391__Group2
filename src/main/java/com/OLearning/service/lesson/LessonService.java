package com.OLearning.service.lesson;

import com.OLearning.entity.Lesson;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LessonService {
    List<Lesson> findAllLessons();

    Lesson findLessonById(Long id);

    Lesson saveLesson(Lesson lesson);

    void deleteLesson(Long id);

    Lesson updateLesson(Long id, Lesson lessonDetails);

    Lesson getCurrentLearningLesson(Long userId, Long courseId);
    int countLessonsInCourse(Long courseId);

    boolean existsById(Long id);
    Optional<Lesson> findById(Long id);

    Lesson getNextLesson(Long userId, Long courseId);
    Lesson findFirstLesson(Long courseId);

    Optional<Lesson> getNextLessonAfterCompleted(Long userId, Long courseId);

    Lesson getNextLessonAfterCurrent(Long currentLessonId, Long courseId);
}