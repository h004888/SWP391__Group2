package com.OLearning.service.lessonCompletion;

import java.util.List;

import com.OLearning.dto.lessonCompletion.LessonCompletionDTO;

import org.springframework.stereotype.Service;

@Service
public interface LessonCompletionService {
    List<LessonCompletionDTO> getByUserAndCourse(Long userId, Long courseId);

    boolean checkLessonCompletion(Long userId, Long lessonId);

    void markLessonAsCompleted(Long userId, Long lessonId);

    Double getOverallProgressOfUser(Long userId, Long courseId);

    Integer getNumberOfCompletedLessons(Long userId, Long courseId);
}
