package com.OLearning.controller.home;

import org.springframework.ui.Model;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.dto.report.ReportCommentDTO;
import com.OLearning.dto.report.ReportCourseDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.User;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.comment.CommentService;
import com.OLearning.service.report.ReportCommentService;
import com.OLearning.service.report.ReportCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CourseDetailController {

    private final CommentService commentService;
    private final ReportCourseService reportCourseService;
    private final ReportCommentService reportCommentService;
    private final CourseRepository courseRepo;
    private final CourseReviewRepository reviewRepo;
    private final UserRepository userRepo;

    @GetMapping("/home/course/{courseId}")
    public String courseDetailPage(@PathVariable Long courseId, Model model, Principal principal) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        List<CourseReview> reviews = reviewRepo.findByCourse(course);
        model.addAttribute("course", course);
        model.addAttribute("reviews", reviews);

        if (principal != null) {
            User user = userRepo.findByEmail(principal.getName()).orElse(null);
            model.addAttribute("user", user);
        }

        model.addAttribute("commentDTO", new CommentDTO());
        model.addAttribute("reportCourseDTO", new ReportCourseDTO());
        model.addAttribute("reportCommentDTO", new ReportCommentDTO());

        model.addAttribute("fragmentContent", "homePage/fragments/courseDetailContent :: courseDetailContent");
        return "homePage/index";
    }

    @PostMapping("/home/course/{courseId}/comment")
    public String postComment(@PathVariable Long courseId, @ModelAttribute CommentDTO dto) {
        commentService.postComment(dto);
        return "redirect:/course/" + courseId;
    }

    @PostMapping("/home/course/{courseId}/report")
    public String submitCourseReport(@PathVariable Long courseId, @ModelAttribute ReportCourseDTO dto) {
        reportCourseService.report(dto);
        return "redirect:/course/" + courseId;
    }

    @PostMapping("/home/course/{courseId}/comment/{commentId}/report")
    public String submitCommentReport(@PathVariable Long courseId, @PathVariable Long commentId, @ModelAttribute ReportCommentDTO dto) {
        reportCommentService.report(dto);
        return "redirect:/course/" + courseId;
    }
}
