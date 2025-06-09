package com.OLearning.service.lesson;

import com.OLearning.dto.instructorDashBoard.LessonDTO;

import java.util.List;

public interface LessonService {

//   List<LessonDTO> getAllLessons();

    LessonDTO getLessonById(Long id);

    void saveLesson(LessonDTO dto);

    void deleteLesson(Long id);

    List<LessonDTO> getLessonsByCourseId(Long courseId);


    // Uncomment and use when needed
    // List<LessonDTO> getLessonsByCourseId(Long courseId);
}
