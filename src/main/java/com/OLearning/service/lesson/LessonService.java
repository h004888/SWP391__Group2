package com.OLearning.service.lesson;

import com.OLearning.dto.lesson.LessonDTO;
import com.OLearning.entity.Lesson;

import java.util.List;

public interface LessonService {
    Lesson createLesson(LessonDTO lessonDTO);
    List<Lesson> findLessonsByChapterId(Long chapterId);
}
