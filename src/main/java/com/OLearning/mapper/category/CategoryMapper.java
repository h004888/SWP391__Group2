package com.OLearning.mapper.category;

import com.OLearning.dto.category.CategoryDTO;
import com.OLearning.entity.Category;

public class CategoryMapper {
    public CategoryDTO toCategoryDTO(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryDTO(category.getId(), category.getName());
    }

    public Category toCategory(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            return null;
        }
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());
        return category;
    }
}
