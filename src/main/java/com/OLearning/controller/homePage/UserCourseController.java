package com.OLearning.controller.homePage;

import java.security.Principal;
import java.util.List;

import com.OLearning.entity.Course;
import com.OLearning.service.enrollment.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.OLearning.entity.User;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.user.UserService;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/learning")
public class UserCourseController {
    @Autowired
    private UserService userService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping
    public String showUserDashboard(Principal principal, Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = extractCurrentUser(principal);
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("categories", categoryService.getListCategories().stream().limit(5).toList());
        model.addAttribute("user", currentUser);
        List<Course> courses = enrollmentService.getCoursesByUserId(currentUser.getUserId());
        model.addAttribute("courses", courses); // nếu cần hiển thị list

        if (!courses.isEmpty()) {
            model.addAttribute("course", courses.get(0)); // hoặc getFirst() nếu bạn dùng ListDeque
        } else {
            model.addAttribute("course", null); // hoặc ẩn phần này trên giao diện
        }

        return "userPage/index-3";
    }

    /**
     * Trích xuất đối tượng User từ Principal nếu có xác thực.
     */
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
