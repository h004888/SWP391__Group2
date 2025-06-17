package com.OLearning.controller.homePage;

import java.util.List;
import java.util.stream.Collectors;

import com.OLearning.dto.CourseDTO;
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
@RequestMapping("/")
public class HomeController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CourseService courseService;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("categories", categoryService.findTop5ByOrderByIdAsc());
        model.addAttribute("allCategories", categoryService.getAllCategory());
        model.addAttribute("topCourses", courseService.getTopCourses().stream().limit(5).collect(Collectors.toList()));

        return "homePage/index";
    }

    @GetMapping("/courses")
    public String courses(Model model, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseDTO> courses = courseService.getCoursesByTotalRatings(pageable); // lưu ý trả về Page<CourseDTO>
        model.addAttribute("categories", categoryService.findTop5ByOrderByIdAsc());
        model.addAttribute("courses", courses.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", courses.getTotalPages());
        return "homePage/course-list";
    }

    @GetMapping("/coursesGrid")
    public String coursesGrid(Model model, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseDTO> courses = courseService.getCoursesByTotalRatings(pageable); // lưu ý trả về Page<CourseDTO>
        model.addAttribute("categories", categoryService.getListCategories());
        model.addAttribute("courses", courses.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", courses.getTotalPages());
        model.addAttribute("totalItems", courses.getTotalElements());
        return "homePage/course-grid";
    }

    @GetMapping("/course-detail")
    public String courseDetail(@RequestParam("id") Long id, Model model) {
        Course course = courseService.findById(id);
        model.addAttribute("course", course);
        return "homePage/course-detail";
    }

}
