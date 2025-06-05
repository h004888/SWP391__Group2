package com.OLearning.service.instructorDashBoard;

import com.OLearning.dto.instructorDashboard.LessonDTO;
import com.OLearning.entity.Lessons;

import java.util.List;

public interface LessonService {
    Lessons createLesson(LessonDTO lessonDTO);
    List<Lessons> findLessonsByChapterId(Long chapterId);
}
