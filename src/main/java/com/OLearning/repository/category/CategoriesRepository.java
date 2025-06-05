package com.OLearning.repository.category;

import com.OLearning.entity.Categories;
import com.OLearning.entity.Enrollment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
    Page<Categories> findAll(Pageable pageable);

    Categories findByName(String name);

    Categories findById(int id);

    boolean existsByName(String name);

    boolean existsById(int id);

    Page<Categories> findByNameContaining(String name, Pageable pageable);

    Categories save(Categories categories);

    void deleteById(int id);

    void delete(Categories categories);

    List<Categories> findByNameContaining(String name);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Categories c SET c.name = :name WHERE c.id = :id")
    void updateCategory(@Param("id") int id, @Param("name") String name);

}
