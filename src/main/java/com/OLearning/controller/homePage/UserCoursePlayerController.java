package com.OLearning.controller.homePage;

import com.OLearning.entity.User;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.enrollment.EnrollmentService;
import com.OLearning.service.lesson.LessonService;
import com.OLearning.service.lessonCompletion.LessonCompletionService;
import com.OLearning.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/learning/course")
public class UserCoursePlayerController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private LessonCompletionService lessonCompletionService;
    @Autowired
    private UserService userService;
    @Autowired
    private EnrollmentService enrollmentService;

    private User extractCurrentUser(Principal principal) {
        if (principal instanceof Authentication authentication) {
            Object principalObj = authentication.getPrincipal();
            if (principalObj instanceof CustomUserDetails customUserDetails) {
                return customUserDetails.getUserEntity();
            }
        }
        return null;
    }
    @RequestMapping("/{courseId}")
    public String showVideoPlayer(@PathVariable("courseId") Long courseId, @RequestParam(value = "lessonId", required = false) Long lessonId
            , Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User currentUser = extractCurrentUser(principal);
        if (currentUser == null) {
            return "redirect:/login";
        }

        boolean hasEnrolled = enrollmentService.hasEnrolled(currentUser.getUserId(), courseId);
        if (!hasEnrolled) {
            return "redirect:/learning";
        }



        return "userPage/course-video-player";
    }
}
