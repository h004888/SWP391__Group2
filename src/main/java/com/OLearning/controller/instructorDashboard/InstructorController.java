package com.OLearning.controller.instructorDashboard;


import com.OLearning.dto.instructorDashboard.CourseDTO;
import com.OLearning.service.instructorDashBoard.CategoryService;
import com.OLearning.service.instructorDashBoard.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
//           return list;
        //bay gio can tim all course theo category nua
    }
//    @Autowired
//    CategoryService categoryService;
//    @PostMapping("/instructordashboard/viewcourse/addcourse")
//    private String addCourseLogic(Model model, @ModelAttribute("course") CourseDTO courseDTO) {
//        //addCourseControl
//    }

    @GetMapping("/instructordashboard")
    public String dashboard() {
        return "instructorDashboard/index";
    }


//    @Autowired
//    private CourseService courseService;
//
//    @PostMapping("/create")
//    public ResponseEntity<String> createCourse(@RequestParam Long userId) {
//        if (!courseService.canCreateCourse(userId)) {
//            return ResponseEntity.badRequest().body("❌ Bạn chưa đăng ký gói hoặc đã hết lượt tạo khóa học.");
//        }
//
//        // Logic tạo khóa học ở đây
//        return ResponseEntity.ok("✅ Tạo khóa học thành công!");
//    }


    @GetMapping("/instructordashboard/viewcourse/addcourse")
    public String addNewCourse(@RequestParam(name = "id", defaultValue = "2") Long id, RedirectAttributes redirectAttributes) {
        //Gui add course
        //Validate BuyPackages
        boolean checkBuyPackages = courseService.canCreateCourse(id);
        if(!checkBuyPackages) {
            redirectAttributes.addFlashAttribute("canCreate", "Can not add new course because package is expired");
            return "redirect:/instructordashboard/viewcourse";

        }
            return "instructorDashboard/addCourse";
    }
}
