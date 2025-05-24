package com.OLearning.service.adminDashBoard;

import com.OLearning.entity.Categories;
import com.OLearning.repository.adminDashBoard.CategoriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriesService {
    @Autowired
    private final CategoriesRepository categoriesRepository;

    public List<Categories> getListCategories(){
        return categoriesRepository.findAll();
    }
}
