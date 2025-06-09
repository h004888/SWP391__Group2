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

@Repository
public interface CategoriesRepository extends JpaRepository<Category, Integer> {
    Page<Category> findAll(Pageable pageable);

    Category findByName(String name);

    Category findById(int id);

    boolean existsByName(String name);

    boolean existsById(int id);

    Page<Category> findByNameContaining(String name, Pageable pageable);

    Category save(Category categories);

    void deleteById(int id);

    void delete(Category categories);

    List<Category> findByNameContaining(String name);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Category c SET c.name = :name WHERE c.id = :id")
    void updateCategory(@Param("id") int id, @Param("name") String name);

    List<Category> findTop5ByOrderByIdAsc();

}
