package com.OLearning.controller.homePage;

import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Enrollment;
import com.OLearning.entity.User;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.courseReview.CourseReviewService;
import com.OLearning.service.enrollment.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/home/course-review")
public class ReviewController {
    @Autowired
    private CourseReviewService courseReviewService;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/add")
    public String addReview(@RequestParam Long courseId,
                           @RequestParam int rating,
                           @RequestParam String comment,
                           @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return "redirect:/home";
        }
        java.util.Optional<Enrollment> enrollmentOpt = enrollmentService.findByUserAndCourse(user, course);
        if (enrollmentOpt.isEmpty()) {
            return "redirect:/home/course-detail?id=" + courseId;
        }
        Enrollment enrollment = enrollmentOpt.get();
        CourseReview review = new CourseReview();
        review.setEnrollment(enrollment);
        review.setCourse(course);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        courseReviewService.save(review);
        return "redirect:/home/course-detail?id=" + courseId;
    }
}
