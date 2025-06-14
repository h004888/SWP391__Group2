package com.OLearning.service.lesson;

import com.OLearning.dto.lesson.LessonTitleDTO;
import com.OLearning.dto.lesson.LessonVideoDTO;
import com.OLearning.entity.Lesson;

import java.util.List;

public interface LessonService {
    List<Lesson> findLessonsByChapterId(Long chapterId);
    void updateContentType(Long lessonId, String contenType);
}
