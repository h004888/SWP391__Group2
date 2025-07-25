package com.OLearning.repository;

import com.OLearning.entity.Category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findAll(Pageable pageable);

    Category findByName(String name);

    Optional<Category> findById(Long id);

    boolean existsByName(String name);

    boolean existsById(Long id);

    Page<Category> findByNameContaining(String name, Pageable pageable);

    Category save(Category categories);

    void deleteById(Long id);

    void delete(Category categories);

    List<Category> findByNameContaining(String name);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Category c SET c.name = :name WHERE c.id = :id")
    void updateCategory(@Param("id") Long id, @Param("name") String name);

    List<Category> findTop5ByOrderByIdAsc();

    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("""
    SELECT c FROM Category c 
    WHERE EXISTS (
        SELECT 1 FROM Course co 
        WHERE co.category.id = c.id 
        AND LOWER(co.status) = 'publish'
    )
    ORDER BY (
        SELECT COUNT(co) FROM Course co 
        WHERE co.category.id = c.id 
        AND LOWER(co.status) = 'publish'
    ) DESC
    """)
    List<Category> findTopCategoriesByCourseCount(Pageable pageable);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT c, COUNT(cs) as courseCount FROM Category c LEFT JOIN c.courses cs GROUP BY c.id, c.name")
    List<Object[]> findAllWithCourseCount();

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.courses")
    List<Category> findAllWithCourses();
}
