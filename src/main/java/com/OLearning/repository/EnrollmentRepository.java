
package com.OLearning.repository;

import com.OLearning.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.OLearning.entity.Enrollment;

import jakarta.transaction.Transactional;

import java.util.List;

@Repository
@Transactional
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.courseId = :courseId AND YEAR(e.enrollmentDate) = :year AND MONTH(e.enrollmentDate) = :month")
    Long countByCourseIdAndMonth(Long courseId, int year, int month);

    @Query("SELECT e.course FROM Enrollment e WHERE e.user.userId = :userId")
    List<Course> findCoursesByUserId(Long userId);
}
