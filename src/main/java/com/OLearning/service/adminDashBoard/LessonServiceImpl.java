package com.OLearning.service.adminDashBoard;

import com.OLearning.entity.Lesson;
import com.OLearning.repository.adminDashBoard.LessonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonServiceImpl implements LessonService {

    @Autowired
    private LessonRepo lessonRepo;

    @Override
    public List<Lesson> getAllLessons() {
        return lessonRepo.findAll();
    }

    @Override
    public List<Lesson> getLessonsByCourseId(int courseId) {
        return lessonRepo.findByCourse_CourseID(courseId);
    }

    @Override
    public Lesson getLessonById(int id) {
        return lessonRepo.findById(id).orElse(null);
    }

    @Override
    public Lesson saveLesson(Lesson lesson) {
        return lessonRepo.save(lesson);
    }

    @Override
    public void deleteLesson(int id) {
        lessonRepo.deleteById(id);
    }
}
