package com.OLearning.repository;

import com.OLearning.entity.Course;
import com.OLearning.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    //tao the nay la xong roi no lay ve mot dong ham roi
    List<Course> findByInstructorUserId(Long userId);

    List<Course> findByStatus(String status);

    Page<Course> findByInstructorUserId(Long userId, Pageable pageable);

    Page<Course> findAll(Pageable pageable);

    //thanh search
    @Query("""
            SELECT c FROM Course c
            WHERE
                (:title IS NULL OR :title = '' OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')))
                AND (:userId IS NULL OR c.instructor.userId = :userId)
            """)
    Page<Course> searchCoursesByKeyWord(
            @Param("title") String title,
            @Param("userId") Long userId,
            Pageable pageable
    );
    //phan trang theo status

    @Query("SELECT c FROM Course c WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR c.category.id = :categoryId) AND " +
            "(:price IS NULL OR :price = '' OR " +
            "(:price = 'free' AND c.price = 0) OR " +
            "(:price = 'paid' AND c.price > 0) OR " +
            "(:price = 'low' AND c.price < 50) OR " +
            "(:price = 'mid' AND c.price BETWEEN 50 AND 100) OR " +
            "(:price = 'high' AND c.price > 100)) AND " +
            "((:status IS NULL OR :status = '') OR " +
            "(:status = 'draft' AND c.status IS NULL) OR " +
            "(:status != 'draft' AND :status != '' AND c.status = :status))")
    Page<Course> filterCourses(@Param("keyword") String keyword,
                               @Param("categoryId") Integer categoryId,
                               @Param("price") String price,
                               @Param("status") String status,
                               Pageable pageable);



    @Query("""
    SELECT c FROM Course c
    WHERE (:instructorId IS NULL OR c.instructor.userId = :instructorId)
      AND (:categoryId IS NULL OR c.category.id = :categoryId)
      AND (:status IS NULL OR :status = '' OR c.status = :status OR (c.status IS NULL AND :status = 'unknown'))
      AND (
           :price IS NULL OR :price = '' OR
           (:price = 'free' AND c.price = 0) OR
           (:price = 'paid' AND c.price > 0) OR
           (:price = 'low' AND c.price > 0 AND c.price < 50) OR
           (:price = 'mid' AND c.price >= 50 AND c.price <= 100) OR
           (:price = 'high' AND c.price > 100)
      )
    """)
    Page<Course> findCoursesByFilters(
            @Param("instructorId") Long instructorId,
            @Param("categoryId") Integer categoryId,
            @Param("status") String status,
            @Param("price") String price,
            Pageable pageable);

}
