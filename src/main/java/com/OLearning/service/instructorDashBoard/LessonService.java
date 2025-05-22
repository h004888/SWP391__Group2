package com.OLearning.service.instructorDashBoard;

import com.OLearning.dto.instructorDashBoard.LessonDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Lesson;
import com.OLearning.mapper.instructorDashBoard.LessonMapper;
import com.OLearning.repository.adminDashBoard.CourseRepo;
import com.OLearning.repository.instructorDashBoard.LessonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonService {

    @Autowired
    private LessonRepo lessonRepo;

    @Autowired
    private CourseRepo courseRepo;

    public List<LessonDTO> getAllLessons() {
        return lessonRepo.findAll().stream()
                .map(LessonMapper::toDTO)
                .collect(Collectors.toList());
    }

    public LessonDTO getLessonById(Long id) {
        Lesson lesson = lessonRepo.findById(id).orElse(null);
        return lesson != null ? LessonMapper.toDTO(lesson) : null;
    }

    public void saveLesson(LessonDTO dto) {
        Course course = courseRepo.findById(dto.getCourseID()).orElse(null);
        Lesson lesson = LessonMapper.toEntity(dto, course);
        lessonRepo.save(lesson);
    }

    public void deleteLesson(Long id) {
        lessonRepo.deleteById(id);
    }

    public List<LessonDTO> getLessonsByCourseId(Long courseId) {
        return lessonRepo.findByCourse_CourseID(courseId).stream()
                .map(LessonMapper::toDTO)
                .collect(Collectors.toList());
    }
}
