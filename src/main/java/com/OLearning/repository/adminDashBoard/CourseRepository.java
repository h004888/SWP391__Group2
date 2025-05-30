package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c WHERE " +
            "(:keyword IS NULL OR LOWER(c.title) LIKE %:keyword%) AND " +
            "(:categoryId IS NULL OR c.category.id = :categoryId) AND " +
            "(:price IS NULL OR " +
            "(:price = 'free' AND c.price = 0) OR " +
            "(:price = 'paid' AND c.price > 0) OR " +
            "(:price = 'low' AND c.price < 50) OR " +
            "(:price = 'mid' AND c.price BETWEEN 50 AND 100) OR " +
            "(:price = 'high' AND c.price > 100)) AND " +
            "(:status IS NULL OR LOWER(c.status) = LOWER(:status))")
    List<Course> filterCourses(@Param("keyword") String keyword,
                               @Param("categoryId") Integer categoryId,
                               @Param("price") String price,
                               @Param("status") String status);

}
