package com.OLearning.repository;

import com.OLearning.dto.course.CourseViewDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c LEFT JOIN c.enrollments e GROUP BY c ORDER BY COUNT(e) DESC")
    List<Course> findAllOrderByStudentCountDesc();

    // function search + filter + sort
    @Query("""
                SELECT c FROM Course c
                WHERE (:keyword       IS NULL
                       OR LOWER(c.title)       LIKE LOWER(CONCAT('%', :keyword, '%'))
                       OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
                  AND (:categoryIds  IS NULL OR c.category.id    IN :categoryIds)
                  AND (
                       :priceFilters IS NULL
                       OR (
                           ('Free' IN (:priceFilters)  AND c.price = 0)
                        OR ('Paid' IN (:priceFilters)  AND c.price > 0)
                       )
                  )
                  AND (
                       :levels IS NULL
                    OR c.courseLevel IN :levels
                  )
            """)
    Page<Course> searchCourses(
            @Param("keyword") String keyword,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("priceFilters") List<String> priceFilters,
            @Param("levels") List<String> levels,
            Pageable pageable);

    // find course by category id
    List<Course> findByCategoryId(int categoryId);

    @Query(value = """
                SELECT TOP 1 c.*
                FROM Enrollments e
                JOIN Courses c ON e.CourseID = c.CourseID
                WHERE e.UserID = :userId
                  AND e.Progress < 100
                ORDER BY e.EnrollmentDate ASC
            """, nativeQuery = true)
    Course findMostRecentIncompleteCourse(@Param("userId") Long userId);




}
