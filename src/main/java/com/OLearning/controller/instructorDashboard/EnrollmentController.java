package com.OLearning.controller.instructorDashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import com.OLearning.service.instructorDashboard.EnrollmentService;
import com.OLearning.entity.Enrollment;
import java.util.List;

@Controller
@RequestMapping("/instructordashboard")
public class EnrollmentController {
    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/enrollment")
    public String enrollment(Model model) {
        List<Enrollment> enrollments = enrollmentService.findAll();
        System.out.println("enrollments: " + enrollments);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/enrollment");
        return "instructorDashboard/index";
    }
}
