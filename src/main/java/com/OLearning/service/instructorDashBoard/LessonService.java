package com.OLearning.service.instructorDashBoard;

import com.OLearning.dto.instructorDashboard.LessonDTO;
import com.OLearning.entity.Lessons;

public interface LessonService {
    Lessons createLesson(LessonDTO lessonDTO);
}
