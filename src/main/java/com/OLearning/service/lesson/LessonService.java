package com.OLearning.service.lesson;

import com.OLearning.entity.Lesson;

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
}