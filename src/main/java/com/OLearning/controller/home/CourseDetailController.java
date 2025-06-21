package com.OLearning.controller.home;

import com.OLearning.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.OLearning.mapper.comment.CommentMapper;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CourseDetailController {

    private final CommentService commentService;
    private final ReportCourseService reportCourseService;
    private final ReportCommentService reportCommentService;
    private final CourseRepository courseRepo;
    private final CourseReviewRepository reviewRepo;
    private final UserRepository userRepo;
    private final CommentMapper commentMapper;

    @GetMapping("/course/{courseId}")
    public String courseDetailPage(@PathVariable Long courseId,
                                   Model model,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Lấy tất cả comment cha (không phải reply)
        List<CourseReview> parentReviews = reviewRepo.findByCourseAndParentReviewIsNull(course);
        // Nạp children cho từng comment cha
        List<CommentDTO> parentDTOs = new java.util.ArrayList<>();
        for (CourseReview parent : parentReviews) {
            List<CourseReview> children = reviewRepo.findByParentReview(parent);
            parent.setChildren(children);
            CommentDTO parentDTO = commentMapper.toDTO(parent);
            // Nạp children cho DTO (nếu cần hiển thị dạng cây DTO)
            if (children != null && !children.isEmpty()) {
                java.util.List<CommentDTO> childDTOs = new java.util.ArrayList<>();
                for (CourseReview child : children) {
                    CommentDTO childDTO = commentMapper.toDTO(child);
                    // Đệ quy nạp children cho childDTO nếu có
                    List<CourseReview> grandChildren = reviewRepo.findByParentReview(child);
                    if (grandChildren != null && !grandChildren.isEmpty()) {
                        List<CommentDTO> grandChildDTOs = new java.util.ArrayList<>();
                        for (CourseReview grandChild : grandChildren) {
                            grandChildDTOs.add(commentMapper.toDTO(grandChild));
                        }
                        childDTO.setChildren(grandChildDTOs);
                    }
                    childDTOs.add(childDTO);
                }
                parentDTO.setChildren(childDTOs);
            }
            parentDTOs.add(parentDTO);
        }
        model.addAttribute("course", course);
        model.addAttribute("reviews", parentDTOs);

        if (userDetails != null) {
            model.addAttribute("user", userDetails.getUser()); // đầy đủ User
        }

        model.addAttribute("commentDTO", new CommentDTO());
        model.addAttribute("reportCourseDTO", new ReportCourseDTO());
        model.addAttribute("reportCommentDTO", new ReportCommentDTO());

        model.addAttribute("fragmentContent", "homePage/fragments/courseDetailContent :: courseDetailContent");
        return "homePage/index";
    }

    // REST API endpoints cho AJAX
    @PostMapping("/api/course/{courseId}/comment")
    @ResponseBody
    public ResponseEntity<?> postCommentAjax(@PathVariable Long courseId,
                                             @RequestBody CommentDTO dto,
                                             Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please login to post a comment"));
        }
        
        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "User not found"));
        }
        
        dto.setUserId(user.getUserId());
        dto.setCourseId(courseId);
        
        // Validate comment
        if (dto.getComment() == null || dto.getComment().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Comment cannot be empty"));
        }
        
        try {
            commentService.postComment(dto);
            return ResponseEntity.ok(Map.of("success", "Comment posted successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/course/{courseId}/report")
    @ResponseBody
    public ResponseEntity<?> submitCourseReportAjax(@PathVariable Long courseId,
                                                    @RequestBody ReportCourseDTO dto,
                                                    Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please login to report a course"));
        }
        
        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "User not found"));
        }
        
        dto.setUserId(user.getUserId());
        dto.setCourseId(courseId);
        
        // Validate reason
        if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please provide a reason for reporting this course"));
        }
        
        try {
            reportCourseService.report(dto);
            return ResponseEntity.ok(Map.of("success", "Course report submitted successfully. Admin will review it."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to submit report: " + e.getMessage()));
        }
    }

    @PostMapping("/api/course/{courseId}/comment/{commentId}/report")
    @ResponseBody
    public ResponseEntity<?> submitCommentReportAjax(@PathVariable Long courseId,
                                                     @PathVariable Long commentId,
                                                     @RequestBody ReportCommentDTO dto,
                                                     Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please login to report a comment"));
        }
        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "User not found"));
        }
        dto.setUserId(user.getUserId());
        dto.setCourseId(courseId);
        dto.setCommentId(commentId);
        if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please provide a reason for reporting this comment"));
        }
        // Kiểm tra user không được report comment của chính mình
        CourseReview review = reviewRepo.findById(commentId).orElse(null);
        if (review != null && review.getEnrollment().getUser().getUserId().equals(user.getUserId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "You cannot report your own comment."));
        }
        try {
            reportCommentService.report(dto);
            return ResponseEntity.ok(Map.of("success", "Comment report submitted successfully. Admin will review it."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to submit report: " + e.getMessage()));
        }
    }

    @PostMapping("/api/course/{courseId}/comment/{reviewId}/update")
    @ResponseBody
    public ResponseEntity<?> updateCommentAjax(@PathVariable Long courseId,
                                               @PathVariable Long reviewId,
                                               @RequestBody CommentDTO dto,
                                               Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please login to update a comment"));
        }
        
        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "User not found"));
        }
        
        dto.setUserId(user.getUserId());
        dto.setCourseId(courseId);
        dto.setReviewId(reviewId);
        
        // Validate comment
        if (dto.getComment() == null || dto.getComment().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Comment cannot be empty"));
        }
        
        try {
            commentService.updateComment(dto);
            return ResponseEntity.ok(Map.of("success", "Comment updated successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Endpoint để lấy danh sách comment mới nhất
    @GetMapping("/api/course/{courseId}/comments")
    @ResponseBody
    public ResponseEntity<?> getComments(@PathVariable Long courseId, Principal principal) {
        try {
            Course course = courseRepo.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            
            // Lấy tất cả comment cha (không phải reply)
            List<CourseReview> parentReviews = reviewRepo.findByCourseAndParentReviewIsNull(course);
            
            // Nạp children cho từng comment cha
            List<CommentDTO> parentDTOs = new java.util.ArrayList<>();
            for (CourseReview parent : parentReviews) {
                List<CourseReview> children = reviewRepo.findByParentReview(parent);
                parent.setChildren(children);
                CommentDTO parentDTO = commentMapper.toDTO(parent);
                // Nạp children cho DTO
                if (children != null && !children.isEmpty()) {
                    java.util.List<CommentDTO> childDTOs = new java.util.ArrayList<>();
                    for (CourseReview child : children) {
                        CommentDTO childDTO = commentMapper.toDTO(child);
                        childDTOs.add(childDTO);
                    }
                    parentDTO.setChildren(childDTOs);
                }
                parentDTOs.add(parentDTO);
            }
            
            Long currentUserId = null;
            if (principal != null) {
                User user = userRepo.findByEmail(principal.getName()).orElse(null);
                if (user != null) currentUserId = user.getUserId();
            }
            
            return ResponseEntity.ok(Map.of(
                "comments", parentDTOs,
                "currentUserId", currentUserId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Các endpoint cũ (giữ lại để tương thích)
    @PostMapping("/course/{courseId}/comment")
    public String postComment(@PathVariable Long courseId,
                              @ModelAttribute CommentDTO dto,
                              Principal principal,
                              RedirectAttributes redirect) {
        if (principal != null) {
            User user = userRepo.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                dto.setUserId(user.getUserId());
                dto.setCourseId(courseId);
                
                // Validate comment
                if (dto.getComment() == null || dto.getComment().trim().isEmpty()) {
                    redirect.addFlashAttribute("error", "Comment cannot be empty");
                    return "redirect:/course/" + courseId;
                }
                
                // Rating có thể null, không cần validate
                
                try {
                    commentService.postComment(dto);
                    redirect.addFlashAttribute("success", "Comment posted successfully!");
                } catch (RuntimeException e) {
                    redirect.addFlashAttribute("error", e.getMessage());
                }
            } else {
                redirect.addFlashAttribute("error", "User not found");
            }
        } else {
            redirect.addFlashAttribute("error", "Please login to post a comment");
        }
        return "redirect:/course/" + courseId;
    }

    @PostMapping("/course/{courseId}/comment/{reviewId}/update")
    public String updateComment(@PathVariable Long courseId,
                                @PathVariable Long reviewId,
                                @ModelAttribute CommentDTO dto,
                                Principal principal,
                                RedirectAttributes redirect) {
        if (principal != null) {
            User user = userRepo.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                dto.setUserId(user.getUserId());
                dto.setCourseId(courseId);
                dto.setReviewId(reviewId);
                
                // Validate comment
                if (dto.getComment() == null || dto.getComment().trim().isEmpty()) {
                    redirect.addFlashAttribute("error", "Comment cannot be empty");
                    return "redirect:/course/" + courseId;
                }
                
                try {
                    commentService.updateComment(dto);
                    redirect.addFlashAttribute("success", "Comment updated successfully!");
                } catch (RuntimeException e) {
                    redirect.addFlashAttribute("error", e.getMessage());
                }
            } else {
                redirect.addFlashAttribute("error", "User not found");
            }
        } else {
            redirect.addFlashAttribute("error", "Please login to update a comment");
        }
        return "redirect:/course/" + courseId;
    }

    @PostMapping("/course/{courseId}/report")
    public String submitCourseReport(@PathVariable Long courseId, 
                                     @ModelAttribute ReportCourseDTO dto,
                                     Principal principal,
                                     RedirectAttributes redirect) {
        if (principal != null) {
            User user = userRepo.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                dto.setUserId(user.getUserId());
                dto.setCourseId(courseId);
                
                // Validate reason
                if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
                    redirect.addFlashAttribute("error", "Please provide a reason for reporting this course");
                    return "redirect:/course/" + courseId;
                }
                
                try {
                    reportCourseService.report(dto);
                    redirect.addFlashAttribute("success", "Course report submitted successfully. Admin will review it.");
                } catch (RuntimeException e) {
                    redirect.addFlashAttribute("error", "Failed to submit report: " + e.getMessage());
                }
            } else {
                redirect.addFlashAttribute("error", "User not found");
            }
        } else {
            redirect.addFlashAttribute("error", "Please login to report a course");
        }
        return "redirect:/course/" + courseId;
    }

    @PostMapping("/course/{courseId}/comment/{commentId}/report")
    public String submitCommentReport(@PathVariable Long courseId, @PathVariable Long commentId, @ModelAttribute ReportCommentDTO dto) {
        reportCommentService.report(dto);
        return "redirect:/course/" + courseId;
    }

    @DeleteMapping("/api/course/{courseId}/comment/{reviewId}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteComment(@PathVariable Long courseId,
                                           @PathVariable Long reviewId,
                                           Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please login to delete a comment"));
        }
        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "User not found"));
        }
        CourseReview review = reviewRepo.findById(reviewId).orElse(null);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Comment not found"));
        }
        // Chỉ cho phép xóa nếu là chủ comment
        if (!review.getEnrollment().getUser().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You can only delete your own comment"));
        }
        reviewRepo.deleteById(reviewId);
        return ResponseEntity.ok(Map.of("success", "Comment deleted successfully"));
    }
}

