package com.OLearning.controller.instructorDashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class AddCourseController {
     @GetMapping("/instructordashboard/viewcourse/addcourse/step1")
     public String step1() {
         return "instructorDashboard/CreateCourseStep1";
     }
    @GetMapping("/instructordashboard/viewcourse/addcourse/step2")
    public String step2() {
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
}
