package com.OLearning.controller.adminDashboard;

import com.OLearning.service.adminDashBoard.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("admin/course")
    public String getCoursePage(Model model) {
        model.addAttribute("accNamePage", "Management Course");
        model.addAttribute("fragmentContent", "adminDashboard/fragments/courseContent :: courseContent");
        model.addAttribute("listCourse", courseService.getAllCourses());
        return "adminDashboard/index";
    }

    @GetMapping("/admin/course/approve/{id}")
    public String approveCourse(@PathVariable("id") Long id) {
        courseService.approveCourse(id);
        return "redirect:/admin/course";
    }

    @GetMapping("/admin/course/reject/{id}")
    public String rejectCourse(@PathVariable("id") Long id) {
        courseService.rejectCourse(id);
        return "redirect:/admin/course";
    }

    @GetMapping("/admin/course/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseService.deleteCourse(id);
        return "redirect:/admin/course";
    }

    @GetMapping("/admin/course/detail/{id}")
    public String viewCourseDetail(@PathVariable("id") Long id) {
        //
        return "redirect:/admin/course";
    }
}
