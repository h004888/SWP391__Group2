package com.OLearning.controller.instructorDashboard;


import com.OLearning.dto.instructorDashboard.CourseAddDTO;
import com.OLearning.dto.instructorDashboard.CourseDTO;
import com.OLearning.entity.Course;
import com.OLearning.service.instructorDashBoard.CategoryService;
import com.OLearning.service.instructorDashBoard.CourseService;
import com.OLearning.service.instructorDashBoard.InstructorBuyPackagesService;
import com.OLearning.service.instructorDashBoard.PackagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class InstructorController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private CategoryService categoryService;

    //load all course by id of user login
    @GetMapping("/instructordashboard/viewcourse")
    public String viewCourse(@RequestParam(name = "id", defaultValue = "2") Long id, Model model, ModelMap modelMap) {
//        List<CourseDTO> list = courseService.findCourseByUserId(id);
//        model.addAttribute("courses", list);
        List<CourseDTO> courseList = courseService.findCourseByUserId(id);
        modelMap.put("courses", courseList);
        return "instructorDashboard/course";
    }

    //dashboard
    @GetMapping("/instructordashboard")
    public String dashboard() {
        return "instructorDashboard/index";
    }


    //Validate add course with package and view addcourse gui
    @GetMapping("/instructordashboard/viewcourse/addcourse")
    public String addNewCourse(@RequestParam(name = "id", defaultValue = "2") Long id, RedirectAttributes redirectAttributes, Model model) {
        //validate packages
//        boolean checkBuyPackages = courseService.canCreateCourse(id);
//        if (!checkBuyPackages) {
//            redirectAttributes.addFlashAttribute("canCreate", "Can not add new course because package is expired");
//            return "redirect:/instructordashboard/viewcourse";
//
//        }
        model.addAttribute("courseAddDTO", new CourseAddDTO());
        model.addAttribute("categories", categoryService.findAll());
        return "instructorDashboard/addCourse";
    }

    @Autowired
    private InstructorBuyPackagesService buyPackagesService;
    @Autowired
    private PackagesService packagesService;

    //save add course to Db
    @PostMapping("/instructordashboard/viewcourse/addcourse")
    public String saveCourse(@ModelAttribute("courseAddDTO") CourseAddDTO courseAddDTO, @RequestParam(name = "id", defaultValue = "2") Long id) {
        courseService.createCourse(courseAddDTO);
        packagesService.updateCourseCreated(buyPackagesService.findIdPackages(id));
        return "redirect:/instructordashboard/viewcourse";
    }
    //deleteCourse
    @PostMapping("/instructordashboard/viewcourse/deletecourse/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/instructordashboard/viewcourse";
    }
    //update Course
    @GetMapping("/instructordashboard/viewcourse/updatecourse/{id}")
    public String updateCourseById(@PathVariable Long id, Model model) {
        //bay gio minh se fill course by id
        Course course = courseService.findCourseById(id);
        model.addAttribute("course", course);
        model.addAttribute("courseAddDTO", new CourseAddDTO());
        model.addAttribute("categories", categoryService.findAll());
        return "instructorDashboard/updateCourse";
        //xong minh de gan no vao giao dien chinh qua cai Course
    }
}
