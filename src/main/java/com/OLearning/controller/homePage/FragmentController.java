package com.OLearning.controller.homePage;

import com.OLearning.dto.CategoryDTO;
import com.OLearning.mapper.CourseMapper;
import com.OLearning.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/fragments")
public class FragmentController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CourseMapper courseMapper;

    @GetMapping("/category")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategory();
    }

    @GetMapping("/courseByCategory")
    public String getAllCoursesByCategory(@RequestParam("categoryId") int categoryId, Model model) {
        model.addAttribute("courses", categoryService.findById(categoryId).getCourses().stream().limit(8)
                .map(courseMapper::toDTO)
                .collect(Collectors.toList()));

        return "homePage/fragments/cardCourse :: cardCourse";
    }
}
