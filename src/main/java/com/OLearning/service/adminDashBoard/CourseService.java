package com.OLearning.service.adminDashBoard;

import com.OLearning.entity.Course;
import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();
    Course getCourseById(int id);
}
