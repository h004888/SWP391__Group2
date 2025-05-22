package com.OLearning.service.adminDashBoard;

import com.OLearning.entity.Course;
import com.OLearning.repository.adminDashBoard.CourseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepo courseRepo;

    @Override
    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    @Override
    public Course getCourseById(int id) {
        return courseRepo.findById(id).orElse(null);
    }
}

