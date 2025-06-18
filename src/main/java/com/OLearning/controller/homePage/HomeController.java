package com.OLearning.controller.homePage;

import java.util.List;
import java.util.stream.Collectors;

import com.OLearning.dto.category.CategoryDTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.entity.Category;
import com.OLearning.entity.Course;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.course.CourseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CourseService courseService;

    @GetMapping()
    public String getMethodName(Model model) {
        // chia làm 2 danh sách:
        List<CategoryDTO> firstFive = categoryService.getAllCategory().stream().limit(5).toList();
        List<CategoryDTO> nextFive = categoryService.getAllCategory().stream().skip(5).limit(5).toList();
        model.addAttribute("topCourses", courseService.getTopCourses().stream().limit(5).collect(Collectors.toList()));
        model.addAttribute("topCategories", categoryService.findTop5ByOrderByIdAsc());
        model.addAttribute("firstFive", firstFive);
        model.addAttribute("nextFive", nextFive);
        return "homePage/index";
    }

    @GetMapping("/coursesGrid")
    public String coursesGrid(Model model, @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<String> priceFilters,
            @RequestParam(required = false) List<String> levels,
            @RequestParam(defaultValue = "Newest") String sortBy,
            @RequestParam(defaultValue = "9") int size) {
        Page<CourseDTO> courses = courseService.searchCoursesGrid(categoryIds, priceFilters, levels, sortBy, keyword,
                page, size); // lưu ý trả về Page<CourseDTO>
        model.addAttribute("categories", categoryService.getListCategories());
        model.addAttribute("courses", courses.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", courses.getTotalPages());
        model.addAttribute("totalItems", courses.getTotalElements());
        return "homePage/course-grid";
    }

    // @GetMapping("/course-detail")
    // public String courseDetail(@RequestParam("id") Long id, Model model) {
    // Course course = courseService.findById(id);
    // int totalStudents = course.getInstructor().getCourses()
    // .stream()
    // .mapToInt(c -> c.getEnrollments().size())
    // .sum();
    // model.addAttribute("totalStudents", totalStudents);

    // model.addAttribute("course", course);
    // return "homePage/course-detail";
    // }

}
