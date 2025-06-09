package com.OLearning.service.lesson.impl;

import com.OLearning.dto.instructorDashBoard.LessonDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Lesson;
import com.OLearning.mapper.lesson.LessonMapper;
import com.OLearning.repository.CourseRepo;
import com.OLearning.repository.LessonRepo;
import com.OLearning.service.lesson.LessonService;
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
    @Autowired
    private LessonMapper lessonMapper;


    @Override
    public List<LessonDTO> getLessonsByCourseId(Long courseId) {
        List<Lesson> lessons = lessonRepo.findByCourseCourseId(courseId);
        return lessons.stream()
                .map(lessonMapper::toDTO)
                .collect(Collectors.toList());
    }


//    @Override
//    public List<LessonDTO> getAllLessons() {
//        return List.of();
//    }

    @Override
    public LessonDTO getLessonById(Long id) {
        Lesson lesson = lessonRepo.findById(id).orElse(null);
        return lesson != null ? lessonMapper.toDTO(lesson) : null;
    }

    @Override
    public void saveLesson(LessonDTO dto) {
        Course course = courseRepo.findById(dto.getCourseId()).orElse(null);
        if (course == null) {
            throw new IllegalArgumentException("Invalid courseId: " + dto.getCourseId());
        }
        Lesson lesson = lessonMapper.toEntity(dto, course);
        lessonRepo.save(lesson);
    }



    @Override
    public void deleteLesson(Long id) {
        lessonRepo.deleteById(id);
    }


//    @Override
//    public List<LessonDTO> getLessonsByCourseId(Long courseId) {
//        return lessonRepo.findByCourseCourseId(courseId).stream()
//                .map(LessonMapper::toDTO)
//                .collect(Collectors.toList());
//    }
}
