package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.adminDashBoard.CourseDTO;
import com.OLearning.dto.adminDashBoard.CourseDetailDTO;
import com.OLearning.entity.Categories;
import com.OLearning.service.adminDashBoard.CategoriesService;
import com.OLearning.service.adminDashBoard.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/course")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CategoriesService categoriesService;

    @GetMapping
    public String getCoursePage(Model model) {
        List<CourseDTO> listCourse = courseService.getAllCourses();
        List<Categories> listCategories = categoriesService.getListCategories();

        model.addAttribute("accNamePage", "Management Course");
        model.addAttribute("fragmentContent", "adminDashboard/fragments/courseContent :: courseContent");
        model.addAttribute("listCourse", listCourse);
        model.addAttribute("listCategories", listCategories);
        return "adminDashboard/index";
    }

    //Filter with ajax
    @GetMapping("/filter")
    public String filterCourses(@RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Integer category,
                                @RequestParam(required = false) String price,
                                @RequestParam(required = false) String status,
                                Model model) {
        List<CourseDTO> listCourse = courseService.filterCourses(keyword, category, price,status);
        model.addAttribute("listCourse", listCourse);
        return "adminDashboard/fragments/courseContent :: courseTableBody";
    }

    @PostMapping("/approve/{id}")
    public String approveCourse(@PathVariable("id") Long id) {
        courseService.approveCourse(id);
        return "redirect:/admin/course";
    }

    @PostMapping("/reject/{id}")
    public String rejectCourse(@PathVariable("id") Long id) {
        courseService.rejectCourse(id);
        return "redirect:/admin/course";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseService.deleteCourse(id);
        return "redirect:/admin/course";
    }

    @GetMapping("/detail/{id}")
    public String viewCourseDetail(Model model, @PathVariable("id") Long id) {
        model.addAttribute("fragmentContent", "adminDashboard/fragments/courseDetailContent :: courseDetail");
        Optional<CourseDetailDTO> optionalDetail = courseService.getDetailCourse(id);
        if (optionalDetail.isPresent()) {
            model.addAttribute("detailCourse", optionalDetail.get());
            return "adminDashboard/index";
        } else {
            return "redirect:/admin/course";
        }

    }
}
