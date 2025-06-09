package com.OLearning.service.lesson;

import com.OLearning.dto.lesson.LessonVideoDTO;
import com.OLearning.entity.Lesson;

import java.util.List;

public interface LessonService {
    Lesson createLesson(LessonVideoDTO lessonVideoDTO);
    List<Lesson> findLessonsByChapterId(Long chapterId);
}
