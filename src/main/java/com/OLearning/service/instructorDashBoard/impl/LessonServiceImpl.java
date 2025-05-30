package com.OLearning.service.instructorDashboard.impl;

import com.OLearning.dto.instructorDashBoard.LessonDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Lesson;
import com.OLearning.mapper.instructorDashBoard.LessonMapper;
import com.OLearning.repository.adminDashBoard.CourseRepo;
import com.OLearning.repository.instructorDashBoard.LessonRepo;
import com.OLearning.service.instructorDashBoard.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonServiceImpl implements LessonService {

    @Autowired
    private LessonRepo lessonRepo;

    @Autowired
    private CourseRepo courseRepo;


    @Override
    public List<LessonDTO> getLessonsByCourseId(Long courseId) {
        List<Lesson> lessons = lessonRepo.findByCourseCourseId(courseId);
        return lessons.stream()
                .map(lessonMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<LessonDTO> getAllLessons() {
        return List.of();
    }

    @Override
    public LessonDTO getLessonById(Long id) {
        Lesson lesson = lessonRepo.findById(id).orElse(null);
        return lesson != null ? LessonMapper.toDTO(lesson) : null;
    }

    @Override
    public void saveLesson(LessonDTO dto) {
        Course course = courseRepo.findById(dto.getCourseId()).orElse(null);
        Lesson lesson = LessonMapper.toEntity(dto, course);
        lessonRepo.save(lesson);
    }

    @Override
    public void deleteLesson(Long id) {
        lessonRepo.deleteById(id);
    }

    // Uncomment and implement if needed
//    @Override
//    public List<LessonDTO> getLessonsByCourseId(Long courseId) {
//        return lessonRepo.findByCourse_CourseID(courseId).stream()
//                .map(LessonMapper::toDTO)
//                .collect(Collectors.toList());
//    }
}
