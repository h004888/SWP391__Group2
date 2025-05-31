package com.OLearning.controller.instructorDashboard;


import com.OLearning.dto.instructorDashboard.AddCourseStep1DTO;
import com.OLearning.dto.instructorDashboard.AddCourseStep2DTO;
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
    @Autowired
    private InstructorBuyPackagesService buyPackagesService;
    @Autowired
    private PackagesService packagesService;

    //load all course by id of user login
    @GetMapping("/instructordashboard/viewcourse")
    public String viewCourse(@RequestParam(name = "id", defaultValue = "2") Long id, Model model, ModelMap modelMap) {
        List<CourseDTO> courseList = courseService.findCourseByUserId(id);
        modelMap.put("courses", courseList);
        return "instructorDashboard/course";
    }

    //dashboard
    @GetMapping("/instructordashboard")
    public String dashboard() {
        return "instructorDashboard/index";
    }


    //Create, countinue update with step, step
    @GetMapping("/instructordashboard/viewcourse/addcoursestep1")
    public String addNewCourse(@RequestParam(value="id", required = false) Long courseId,Model model) {

        if(courseId == null) {
            model.addAttribute("coursestep1", new AddCourseStep1DTO());
            model.addAttribute("categories", categoryService.findAll());
            return "instructorDashboard/CreateCourseStep1";
        }
        //bay gio khi next minh deo save nua
        //minh se gan dan cai gia tri course vao la duoc
        return "instructorDashboard/index";
//
//        Course course = courseService.findCourseById(courseId);
//        AddCourseStep1DTO coursestep1 = courseService.draftCourseStep1(course);
//        //in duoc het thong tin course ra kieu step 1
//        model.addAttribute("coursestep1", coursestep1);
//        return "instructorDashboard/CreateCourseStep1";

    }
    //NEXT step 2 and save step 1 or draf
    @PostMapping("/instructordashboard/viewcourse/addcoursestep2")
    public String SaveStep1NextToStep2(
            @ModelAttribute("coursestep1") AddCourseStep1DTO courseStep1, @RequestParam(name = "id", defaultValue = "2") Long id, @RequestParam(name="action") String action,  Model model,
            ModelMap modelMap) {



        if(action.equals("draft")) {
            courseService.createCourseStep1(null, courseStep1);
            List<CourseDTO> courseList = courseService.findCourseByUserId(id);
            modelMap.put("courses", courseList);
            return "redirect:/instructordashboard/viewcourse";
        }
        Course course = courseService.createCourseStep1(null, courseStep1);
        Long courseId = course.getCourseId();
        model.addAttribute("courseId", courseId);
        model.addAttribute("coursestep2", new AddCourseStep2DTO());
        return "instructorDashboard/CreateCourseStep2";
    }

    @GetMapping("/instructordashboard/viewcourse/addcourse/step3")
    public String step3() {
        return "instructorDashboard/CreateCourseStep3";
    }
    @GetMapping("/instructordashboard/viewcourse/addcourse/step4")
    public String step4() {
        return "instructorDashboard/CreateCourseStep4";
    }


    //save add course to Db
    @PostMapping("/instructordashboard/viewcourse/addcourse")
    public String saveCourse(@ModelAttribute("courseAddDTO") CourseAddDTO courseAddDTO, @RequestParam(name = "id", defaultValue = "2") Long id) {
//        courseService.createCourse(courseAddDTO);
//        packagesService.updateCourseCreated(buyPackagesService.findIdPackages(id));
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
