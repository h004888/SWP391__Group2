package com.OLearning.controller.homePage;

import com.OLearning.entity.Lesson;
import com.OLearning.entity.User;
import com.OLearning.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.OLearning.service.lesson.LessonService;

import java.security.Principal;

@Controller
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @GetMapping("/lesson/player/{id}")
    public String lessonPlayer(@PathVariable Long lessonId, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = extractCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        Lesson lesson = lessonService.findLessonById(lessonId);
        model.addAttribute("lesson", lesson);
        return "lesson-player";
    }

    private User extractCurrentUser(Principal principal) {
        if (principal instanceof Authentication authentication) {
            Object principalObj = authentication.getPrincipal();
            if (principalObj instanceof CustomUserDetails customUserDetails) {
                return customUserDetails.getUserEntity();
            }
        }
        return null;
    }
}