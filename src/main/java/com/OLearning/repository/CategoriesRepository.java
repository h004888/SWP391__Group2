package com.OLearning.repository;

import com.OLearning.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoriesRepository extends JpaRepository<Category, Integer> {
    Category findByName(String name);

    Category findById(int id);

    boolean existsByName(String name);

    boolean existsById(int id);

    List<Category> findAll();

    Category save(Category category);

    void deleteById(int id);

    void delete(Category category);

    @Modifying
    @Query("UPDATE Category c SET c.name = :name WHERE c.id = :id")
    void updateCategory(@Param("id") int id, @Param("name") String name);
}
