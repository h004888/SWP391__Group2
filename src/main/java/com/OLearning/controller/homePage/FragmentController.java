package com.OLearning.controller.homePage;

import com.OLearning.dto.category.CategoryDTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.entity.Category;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.course.CourseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    private CourseService courseService;

    @GetMapping("/category")
    public List<Category> getAllCategories() {
        return categoryService.findAll();
    }

    @GetMapping("/topCourse")
    public String getTopCourses(Model model) {
        model.addAttribute("topCourses",
                courseService.getTopCourses().stream().limit(8).collect(Collectors.toList()));
        return "homePage/fragments/topCourse :: topCourse";
    }

    @GetMapping("/courseByCategory")
    public String getAllCoursesByCategory(@RequestParam("categoryId") long categoryId, Model model) {
        model.addAttribute("courses", categoryService.findById(categoryId).get().getCourses().stream().limit(8));

        return "homePage/fragments/cardCourse :: cardCourse";
    }

    @GetMapping("/coursesGrid")
    public String courses(
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false, name = "priceFilters") List<String> priceFilters,
            @RequestParam(required = false, name = "levels") List<String> levels, // đổi kiểu
            @RequestParam(defaultValue = "Newest") String sortBy,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        Page<CourseDTO> courses = courseService.searchCoursesGrid(
                categoryIds, priceFilters, levels, sortBy, keyword, page, size);

        model.addAttribute("courses", courses.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", courses.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("priceFilters", priceFilters); // sửa ở đây
        model.addAttribute("categoryIds", categoryIds);

        return "homePage/fragments/mainCourseGrid :: mainCourseGrid";
    }

}
