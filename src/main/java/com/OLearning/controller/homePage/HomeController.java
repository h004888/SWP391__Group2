package com.OLearning.controller.homePage;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import com.OLearning.dto.category.CategoryDTO;
import com.OLearning.dto.course.CourseViewDTO;
import com.OLearning.entity.Course;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
        @Autowired
        private UserService userService;
        @GetMapping()
        public String getMethodName(Model model) {
                // chia làm 2 danh sách:
                List<CategoryDTO> firstFive = categoryService.getAllCategory().stream().limit(5).toList();
                List<CategoryDTO> nextFive = categoryService.getAllCategory().stream().skip(5).limit(5).toList();
                model.addAttribute("topCourses",
                                courseService.getTopCourses().stream().limit(5).collect(Collectors.toList()));
                model.addAttribute("topCategories", categoryService.findTop5ByOrderByIdAsc());
                model.addAttribute("firstFive", firstFive);
                model.addAttribute("nextFive", nextFive);
                model.addAttribute("totalCourseIsPublish", courseService.countCourseIsPublish());
                model.addAttribute("totalInstructor", userService.countInstructor());
                model.addAttribute("totalStudent", userService.countStudent());
                model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderCategory");
                model.addAttribute("fragmentContent", "homePage/fragments/mainContent :: mainContent");
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
                Page<CourseViewDTO> courses = courseService.searchCoursesGrid(categoryIds, priceFilters, levels, sortBy,
                                keyword,
                                page, size); // lưu ý trả về Page<CourseDTO>
                model.addAttribute("categories", categoryService.getListCategories());
                model.addAttribute("courses", courses.getContent());
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", courses.getTotalPages());
                model.addAttribute("totalItems", courses.getTotalElements());
                model.addAttribute("categoryIds", categoryIds);
                model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");

                return "homePage/course-grid";
        }

        @GetMapping("/course-detail")
        public String courseDetail(@RequestParam("id") Long id, Model model) {
                boolean flag = courseService.existsById(id);
                if (!flag) {
                        model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
                        return "homePage/error-404";
                }

                CourseViewDTO course = courseService.getCourseById(id);

                model.addAttribute("totalStudents", course.getEnrollments().size());
                model.addAttribute("courseByInstructor",
                                course.getInstructor().getCourses().stream().map(CourseMapper::toCourseViewDTO)
                                                .collect(Collectors.toList()));
                model.addAttribute("courseByCategory",
                                course.getCategory().getCourses().stream().map(CourseMapper::toCourseViewDTO)
                                                .collect(Collectors.toList()));
                model.addAttribute("course", course);
                model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");

                return "homePage/course-detail";
        }

}
