package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepo extends JpaRepository<Course, Integer> {

}
