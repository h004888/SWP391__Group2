package com.OLearning.repository;

import com.OLearning.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorCategoryRepo extends JpaRepository<Category, Long> {
    Category findByName(String name);

}
