package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.adminDashBoard.CourseDetailDTO;
import com.OLearning.service.adminDashBoard.CategoriesService;
import com.OLearning.service.adminDashBoard.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CategoriesService categoriesService;

    @GetMapping("admin/course")
    public String getCoursePage(Model model) {
        model.addAttribute("accNamePage", "Management Course");
        model.addAttribute("fragmentContent", "adminDashboard/fragments/courseContent :: courseContent");
        model.addAttribute("listCourse", courseService.getAllCourses());
        model.addAttribute("listCategories",categoriesService.getListCategories());
        return "adminDashboard/index";
    }

    @PostMapping("/admin/course/approve/{id}")
    public String approveCourse(@PathVariable("id") Long id) {
        courseService.approveCourse(id);
        return "redirect:/admin/course";
    }

    @PostMapping("/admin/course/reject/{id}")
    public String rejectCourse(@PathVariable("id") Long id) {
        courseService.rejectCourse(id);
        return "redirect:/admin/course";
    }

    @GetMapping ("/admin/course/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseService.deleteCourse(id);
        return "redirect:/admin/course";
    }

    @GetMapping("/admin/course/detail/{id}")
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
