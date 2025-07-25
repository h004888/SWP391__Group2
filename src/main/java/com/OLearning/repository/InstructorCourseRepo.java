package com.OLearning.repository;

import com.OLearning.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("instructorCourseRepo")
public interface InstructorCourseRepo extends JpaRepository<Course, Long> {
    //tao the nay la xong roi no lay ve mot dong ham roi
    List<Course> findByInstructorUserId(Long userId);
}
