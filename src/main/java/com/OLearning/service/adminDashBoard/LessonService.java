package com.OLearning.service.adminDashBoard;

import com.OLearning.entity.Lesson;
import java.util.List;

public interface LessonService {
    List<Lesson> getAllLessons();
    List<Lesson> getLessonsByCourseId(int courseId);
    Lesson getLessonById(int id);
    Lesson saveLesson(Lesson lesson);
    void deleteLesson(int id);
}
