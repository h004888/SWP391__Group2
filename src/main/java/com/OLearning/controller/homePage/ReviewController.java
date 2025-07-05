package com.OLearning.controller.homePage;

import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Enrollment;
import com.OLearning.entity.User;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.courseReview.CourseReviewService;
import com.OLearning.service.enrollment.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/add")
    public String addReview(@RequestParam Long courseId,
                           @RequestParam int rating,
                           @RequestParam String comment,
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
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
        // Lấy enrollment mới nhất cho user và course này
        List<Enrollment> enrollments = enrollmentRepository.findByUserUserId(user.getUserId())
                .stream()
                .filter(e -> e.getCourse().getCourseId().equals(courseId))
                .sorted((e1, e2) -> e2.getEnrollmentDate().compareTo(e1.getEnrollmentDate()))
                .toList();
        
        if (enrollments.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng ký khóa học này để có thể đánh giá!");
            return "redirect:/home/course-detail?id=" + courseId;
        }
        
        Enrollment enrollment = enrollments.get(0); // Lấy enrollment mới nhất
        
        // Kiểm tra xem user đã đánh giá course này chưa
        java.util.Optional<CourseReview> existingReview = courseReviewService.findByEnrollment(enrollment);
        if (existingReview.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Bạn đã đánh giá khóa học này rồi!");
            return "redirect:/home/course-detail?id=" + courseId;
        }
        CourseReview review = new CourseReview();
        review.setEnrollment(enrollment);
        review.setCourse(course);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        courseReviewService.save(review);
        redirectAttributes.addFlashAttribute("success", "Đánh giá của bạn đã được đăng thành công!");
        return "redirect:/home/course-detail?id=" + courseId;
    }
}
