package com.OLearning.controller.home;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.dto.report.ReportCommentDTO;
import com.OLearning.dto.report.ReportCourseDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.User;
import com.OLearning.mapper.comment.CommentMapper;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.comment.CommentService;
import com.OLearning.service.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CourseDetailMinController {

    private final CommentService commentService;
    private final ReportService reportService;
    private final CourseRepository courseRepo;
    private final CourseReviewRepository reviewRepo;
    private final UserRepository userRepo;
    private final CommentMapper commentMapper;

    @GetMapping("/course/{courseId}/min")
    public String courseDetailMinPage(@PathVariable Long courseId,
                                    Model model,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));

        User user = null;
        if (userDetails != null) {
            user = userDetails.getUser();
        }

        // Lấy danh sách reviews và sắp xếp theo thời gian mới nhất
        List<CourseReview> allReviews = reviewRepo.findByCourseWithUserOrderByCreatedAtDesc(course);
        List<CourseReview> parentReviews = allReviews.stream()
                .filter(review -> review.getParentReview() == null)
                .toList();

        // Nạp children cho từng comment cha
        List<CommentDTO> parentDTOs = new java.util.ArrayList<>();
        for (CourseReview parent : parentReviews) {
            List<CourseReview> children = reviewRepo.findByParentReviewOrderByCreatedAtDesc(parent);
            parent.setChildren(children);
            CommentDTO parentDTO = commentMapper.toDTO(parent);
            // Nạp children cho DTO
            if (children != null && !children.isEmpty()) {
                java.util.List<CommentDTO> childDTOs = new java.util.ArrayList<>();
                for (CourseReview child : children) {
                    CommentDTO childDTO = commentMapper.toDTO(child);
                    // Đệ quy nạp children cho childDTO
                    List<CourseReview> grandChildren = reviewRepo.findByParentReviewOrderByCreatedAtDesc(child);
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
        model.addAttribute("user", user);
        model.addAttribute("reviews", parentDTOs);
        model.addAttribute("commentDTO", new CommentDTO());
        model.addAttribute("reportCourseDTO", new ReportCourseDTO());
        model.addAttribute("reportCommentDTO", new ReportCommentDTO());

        return "userPage/course-detail-min";
    }

    @PostMapping("/api/course/{courseId}/comment")
    @ResponseBody
    public ResponseEntity<?> addComment(@PathVariable Long courseId,
                                      @RequestBody CommentDTO dto,
                                      Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Vui lòng đăng nhập để bình luận"));
        }

        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Không tìm thấy người dùng"));
        }

        dto.setUserId(user.getUserId());
        dto.setCourseId(courseId);

        // Validate comment
        if (dto.getComment() == null || dto.getComment().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Bình luận không được để trống"));
        }

        try {
            commentService.addComment(dto, user.getUserId(), courseId);
            return ResponseEntity.ok(Map.of("success", "Đã đăng bình luận thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi đăng bình luận: " + e.getMessage()));
        }
    }

    @PostMapping("/api/course/{courseId}/comment/reply")
    @ResponseBody
    public ResponseEntity<?> replyComment(@PathVariable Long courseId,
                                        @RequestBody CommentDTO dto,
                                        Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Vui lòng đăng nhập để trả lời bình luận"));
        }

        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Không tìm thấy người dùng"));
        }

        dto.setUserId(user.getUserId());
        dto.setCourseId(courseId);

        // Validate comment
        if (dto.getComment() == null || dto.getComment().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Nội dung trả lời không được để trống"));
        }

        try {
            commentService.replyComment(dto, user.getUserId(), courseId);
            return ResponseEntity.ok(Map.of("success", "Đã trả lời bình luận thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi trả lời bình luận: " + e.getMessage()));
        }
    }

    @PostMapping("/api/course/{courseId}/comment/instructor-reply")
    @ResponseBody
    public ResponseEntity<?> instructorReplyFromNotification(@PathVariable Long courseId,
                                                           @RequestBody CommentDTO dto,
                                                           Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Vui lòng đăng nhập để trả lời bình luận"));
        }

        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Không tìm thấy người dùng"));
        }

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));

        // Kiểm tra xem user có phải là instructor của khóa học không
        if (!course.getInstructor().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Bạn không phải là giảng viên của khóa học này"));
        }

        dto.setUserId(user.getUserId());
        dto.setCourseId(courseId);

        try {
            commentService.replyComment(dto, user.getUserId(), courseId);
            return ResponseEntity.ok(Map.of("success", "Đã trả lời bình luận thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi trả lời bình luận: " + e.getMessage()));
        }
    }

    @PutMapping("/api/course/{courseId}/comment/{commentId}/edit")
    @ResponseBody
    public ResponseEntity<?> editComment(@PathVariable Long courseId,
                                       @PathVariable Long commentId,
                                       @RequestBody CommentDTO dto,
                                       Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Vui lòng đăng nhập để sửa bình luận"));
        }

        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Không tìm thấy người dùng"));
        }

        dto.setReviewId(commentId);
        dto.setUserId(user.getUserId());
        dto.setCourseId(courseId);

        try {
            commentService.editComment(dto, user.getUserId(), courseId);
            return ResponseEntity.ok(Map.of("success", "Đã cập nhật bình luận thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi cập nhật bình luận: " + e.getMessage()));
        }
    }

    @DeleteMapping("/api/course/{courseId}/comment/{commentId}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteComment(@PathVariable Long courseId,
                                         @PathVariable Long commentId,
                                         Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Vui lòng đăng nhập để xóa bình luận"));
        }

        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Không tìm thấy người dùng"));
        }

        try {
            commentService.deleteComment(commentId, user.getUserId(), courseId);
            return ResponseEntity.ok(Map.of("success", "Đã xóa bình luận thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi xóa bình luận: " + e.getMessage()));
        }
    }

    @PostMapping("/api/course/{courseId}/report")
    @ResponseBody
    public ResponseEntity<?> reportCourse(@PathVariable Long courseId,
                                        @RequestBody ReportCourseDTO dto,
                                        Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Vui lòng đăng nhập để báo cáo khóa học"));
        }

        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Không tìm thấy người dùng"));
        }

        dto.setUserId(user.getUserId());
        dto.setCourseId(courseId);

        if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Vui lòng cung cấp lý do báo cáo khóa học này"));
        }

        try {
            reportService.reportCourse(dto);
            return ResponseEntity.ok(Map.of("success", "Đã gửi báo cáo thành công. Quản trị viên sẽ xem xét báo cáo của bạn."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi gửi báo cáo: " + e.getMessage()));
        }
    }

    @PostMapping("/api/course/{courseId}/comment/{commentId}/report")
    @ResponseBody
    public ResponseEntity<?> reportComment(@PathVariable Long courseId,
                                         @PathVariable Long commentId,
                                         @RequestBody ReportCommentDTO dto,
                                         Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Vui lòng đăng nhập để báo cáo bình luận"));
        }

        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Không tìm thấy người dùng"));
        }

        dto.setUserId(user.getUserId());
        dto.setCourseId(courseId);
        dto.setCommentId(commentId);

        if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Vui lòng cung cấp lý do báo cáo bình luận này"));
        }

        // Kiểm tra user không được report comment của chính mình
        CourseReview review = reviewRepo.findById(commentId).orElse(null);
        if (review != null && review.getEnrollment().getUser().getUserId().equals(user.getUserId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Bạn không thể báo cáo bình luận của chính mình"));
        }

        try {
            reportService.reportComment(dto);
            return ResponseEntity.ok(Map.of("success", "Đã gửi báo cáo thành công. Quản trị viên sẽ xem xét báo cáo của bạn."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi gửi báo cáo: " + e.getMessage()));
        }
    }

    // Non-AJAX endpoints for form submissions
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
                try {
                    commentService.addComment(dto, user.getUserId(), courseId);
                    redirect.addFlashAttribute("success", "Đã đăng bình luận thành công");
                } catch (RuntimeException e) {
                    redirect.addFlashAttribute("error", "Lỗi khi đăng bình luận: " + e.getMessage());
                }
            } else {
                redirect.addFlashAttribute("error", "Không tìm thấy người dùng");
            }
        } else {
            redirect.addFlashAttribute("error", "Vui lòng đăng nhập để bình luận");
        }
        return "redirect:/course/" + courseId + "/min";
    }

    @PostMapping("/course/{courseId}/comment/{commentId}/update")
    public String updateComment(@PathVariable Long courseId,
                              @PathVariable Long commentId,
                              @ModelAttribute CommentDTO dto,
                              Principal principal,
                              RedirectAttributes redirect) {
        if (principal != null) {
            User user = userRepo.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                dto.setReviewId(commentId);
                dto.setUserId(user.getUserId());
                dto.setCourseId(courseId);
                try {
                    commentService.editComment(dto, user.getUserId(), courseId);
                    redirect.addFlashAttribute("success", "Đã cập nhật bình luận thành công");
                } catch (RuntimeException e) {
                    redirect.addFlashAttribute("error", "Lỗi khi cập nhật bình luận: " + e.getMessage());
                }
            } else {
                redirect.addFlashAttribute("error", "Không tìm thấy người dùng");
            }
        } else {
            redirect.addFlashAttribute("error", "Vui lòng đăng nhập để sửa bình luận");
        }
        return "redirect:/course/" + courseId + "/min";
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
                if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
                    redirect.addFlashAttribute("error", "Vui lòng cung cấp lý do báo cáo khóa học này");
                    return "redirect:/course/" + courseId + "/min";
                }
                try {
                    reportService.reportCourse(dto);
                    redirect.addFlashAttribute("success", "Đã gửi báo cáo thành công. Quản trị viên sẽ xem xét báo cáo của bạn.");
                } catch (RuntimeException e) {
                    redirect.addFlashAttribute("error", "Lỗi khi gửi báo cáo: " + e.getMessage());
                }
            } else {
                redirect.addFlashAttribute("error", "Không tìm thấy người dùng");
            }
        } else {
            redirect.addFlashAttribute("error", "Vui lòng đăng nhập để báo cáo khóa học");
        }
        return "redirect:/course/" + courseId + "/min";
    }

    @PostMapping("/course/{courseId}/comment/{commentId}/report")
    public String submitCommentReport(@PathVariable Long courseId,
                                    @PathVariable Long commentId,
                                    @ModelAttribute ReportCommentDTO dto,
                                    Principal principal,
                                    RedirectAttributes redirect) {
        if (principal != null) {
            User user = userRepo.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                dto.setUserId(user.getUserId());
                dto.setCourseId(courseId);
                dto.setCommentId(commentId);
                if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
                    redirect.addFlashAttribute("error", "Vui lòng cung cấp lý do báo cáo bình luận này");
                    return "redirect:/course/" + courseId + "/min";
                }
                try {
                    reportService.reportComment(dto);
                    redirect.addFlashAttribute("success", "Đã gửi báo cáo thành công. Quản trị viên sẽ xem xét báo cáo của bạn.");
                } catch (RuntimeException e) {
                    redirect.addFlashAttribute("error", "Lỗi khi gửi báo cáo: " + e.getMessage());
                }
            } else {
                redirect.addFlashAttribute("error", "Không tìm thấy người dùng");
            }
        } else {
            redirect.addFlashAttribute("error", "Vui lòng đăng nhập để báo cáo bình luận");
        }
        return "redirect:/course/" + courseId + "/min";
    }
} 