package com.OLearning.controller.instructorDashboard;


import com.OLearning.dto.instructorDashboard.CourseDTO;
import com.OLearning.service.instructorDashBoard.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
@Controller
public class InstructorController {
    @Autowired
    private CourseService courseService;
    @GetMapping("/instructordashboard/viewcourse")
    public String viewCourse(@RequestParam(name = "id", defaultValue = "2") Long id, Model model) {
        List<CourseDTO> list = courseService.findCourseByUserId(id);
        model.addAttribute("courses", list);
        return "instructorDashboard/course"; //tim dc ra all course
        //bay gio can tim all course theo category nua
    }
//    @Autowired CategoryService categoryService;
//    @PostMapping("/instructordashboard/viewcourse/addcourse")
//    private String addCourseLogic(Model model, @ModelAttribute("course") CourseDTO courseDTO) {
//        //addCourseControl
//    }

    @GetMapping("/instructordashboard")
    public String dashboard() {
        return "instructorDashboard/index";
    }
    @GetMapping("/instructordashboard/viewcourse/addcourse")
    public String addNewCourse() {
        return "instructorDashboard/addCourse";
    }

}
