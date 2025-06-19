package com.OLearning.service.lessonCompletion;

import java.util.List;

import com.OLearning.entity.LessonCompletion;

public interface LessonCompletionService {
    List<Long> getCompletedLessonIdsByUser(Long userId);

    boolean isLessonCompleted(Long userId, Long lessonId);
    int countCompletedLessons(Long userId, Long courseId);
}
