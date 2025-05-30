package com.OLearning.repository.instructorDashBoard;

import com.OLearning.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorCategoryRepo extends JpaRepository<Categories, Long> {
    Categories findByName(String name);
}
