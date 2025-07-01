package com.OLearning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.OLearning.entity.Enrollment;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByUserUserId(Long userId);

    boolean existsByUser_UserIdAndCourse_CourseId(Long userId, Long courseId);

    @Query(
            value = """
                    SELECT DATEDIFF(WEEK, e.EnrollmentDate, GETDATE()) 
                    FROM Enrollments e 
                    WHERE e.UserID = :userId AND e.CourseID = :courseId
                    """,
            nativeQuery = true
    )
    Integer getWeeksEnrolled(@Param("userId") Long userId, @Param("courseId") Long courseId);


}
