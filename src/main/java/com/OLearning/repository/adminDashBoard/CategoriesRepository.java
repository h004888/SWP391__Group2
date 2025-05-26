package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
    Categories findByName(String name);

    Categories findById(int id);

    boolean existsByName(String name);

    boolean existsById(int id);

    List<Categories> findAll();

    Categories save(Categories categories);

    void deleteById(int id);

    void delete(Categories categories);

    @Modifying
    @Query("UPDATE Categories c SET c.name = :name WHERE c.id = :id")
    void updateCategory(@Param("id") int id, @Param("name") String name);
}
