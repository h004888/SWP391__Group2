package com.OLearning.controller.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class homeController {

    @GetMapping("/home")
    public String getHomePage(){
        return"homePage/home";
    }
}
