package com.OLearning.controller.homePage;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Report;
import com.OLearning.entity.User;
import com.OLearning.entity.Lesson;
import com.OLearning.entity.Notification;
import com.OLearning.mapper.comment.CommentMapper;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.repository.ReportRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.repository.LessonRepository;
import com.OLearning.service.comment.CommentService;
import com.OLearning.service.notification.NotificationService;
import com.OLearning.dto.report.ReportCourseDTO;
import com.OLearning.service.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Comparator;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final CourseReviewRepository reviewRepo;
    private final ReportRepository reportRepo;
    private final CommentMapper commentMapper;
    private final LessonRepository lessonRepo;
    private final NotificationService notificationService;
    @Autowired
    private ReportService reportService;

    @PostMapping("/{courseId}/comment")
    @ResponseBody
    public ResponseEntity<?> addComment(@PathVariable Long courseId,
                                      @ModelAttribute CommentDTO dto,
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
            CourseReview comment = commentService.addComment(dto, user.getUserId(), courseId);
            // Gửi notification cho instructor
            Course course = courseRepo.findById(courseId).orElse(null);
            if (course != null && course.getInstructor() != null && !course.getInstructor().getUserId().equals(user.getUserId())) {
                Notification notification = new Notification();
                notification.setType("COMMENT");
                notification.setUser(course.getInstructor());
                notification.setCourse(course);
                notification.setCommentId(comment.getReviewId()); // truyền commentId vừa tạo
                notification.setMessage(user.getFullName() + " đã bình luận vào khóa học: " + course.getTitle());
                notification.setSentAt(LocalDateTime.now());
                notification.setStatus("failed");
                notificationService.sendMess(notification);
            }
            return ResponseEntity.ok(Map.of("success", "Đã đăng bình luận thành công"));
        } catch (RuntimeException e) {
            System.err.println("Error adding comment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi đăng bình luận: " + e.getMessage()));
        }
    }

    @PostMapping("/{courseId}/comment/reply")
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
                    .body(Map.of("error", "Bình luận không được để trống"));
        }

        if (dto.getParentId() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Thiếu thông tin bình luận gốc"));
        }

        try {
            commentService.replyComment(dto, user.getUserId(), courseId);
            // Gửi notification cho user của comment gốc
            if (dto.getParentId() != null) {
                CourseReview parent = reviewRepo.findById(dto.getParentId()).orElse(null);
                if (parent != null && parent.getEnrollment() != null && parent.getEnrollment().getUser() != null) {
                    User parentUser = parent.getEnrollment().getUser();
                    if (!parentUser.getUserId().equals(user.getUserId())) {
                        Notification notification = new Notification();
                        notification.setType("COMMENT");
                        notification.setUser(parentUser);
                        notification.setCourse(parent.getCourse());
                        notification.setCommentId(parent.getReviewId());
                        notification.setMessage(user.getFullName() + " đã trả lời bình luận của bạn trong khóa học: " + parent.getCourse().getTitle());
                        notification.setSentAt(LocalDateTime.now());
                        notification.setStatus("failed");
                        notificationService.sendMess(notification);
                    }
                }
            }
            return ResponseEntity.ok(Map.of("success", "Đã trả lời bình luận thành công"));
        } catch (RuntimeException e) {
            System.err.println("Error replying comment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi trả lời bình luận: " + e.getMessage()));
        }
    }

    @PostMapping("/{courseId}/comment/instructor-reply")
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

    @PostMapping("/{courseId}/comment/{commentId}/edit")
    @ResponseBody
    public ResponseEntity<?> editComment(@PathVariable Long courseId,
                                       @PathVariable Long commentId,
                                       @ModelAttribute CommentDTO dto,
                                       Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Vui lòng đăng nhập để chỉnh sửa bình luận"));
        }

        User user = userRepo.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Không tìm thấy người dùng"));
        }

        dto.setReviewId(commentId);
        dto.setUserId(user.getUserId());
        dto.setCourseId(courseId);

        // Validate comment
        if (dto.getComment() == null || dto.getComment().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Bình luận không được để trống"));
        }

        try {
            commentService.editComment(dto, user.getUserId(), courseId);
            return ResponseEntity.ok(Map.of("success", "Đã cập nhật bình luận thành công"));
        } catch (RuntimeException e) {
            System.err.println("Error editing comment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi cập nhật bình luận: " + e.getMessage()));
        }
    }

    @PutMapping("/{courseId}/comment/{commentId}/update")
    @ResponseBody
    public ResponseEntity<?> updateComment(@PathVariable Long courseId,
                                         @PathVariable Long commentId,
                                         @RequestBody CommentDTO dto,
                                         Principal principal) {
        // Alias for editComment to maintain backward compatibility
        return editComment(courseId, commentId, dto, principal);
    }

    @DeleteMapping("/{courseId}/comment/{commentId}/delete")
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
            commentService.deleteComment(commentId, user.getUserId());
            return ResponseEntity.ok(Map.of("success", "Đã xóa bình luận thành công"));
        } catch (RuntimeException e) {
            System.err.println("Error deleting comment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi xóa bình luận: " + e.getMessage()));
        }
    }

    @PostMapping("/{courseId}/comment/{commentId}/report")
    @ResponseBody
    public ResponseEntity<?> reportComment(@PathVariable Long courseId,
                                         @PathVariable Long commentId,
                                         @RequestBody Map<String, String> request,
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

        String reason = request.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lý do báo cáo không được để trống"));
        }

        try {
            commentService.reportComment(commentId, user.getUserId(), reason.trim());
            return ResponseEntity.ok(Map.of("success", "Đã báo cáo bình luận thành công"));
        } catch (RuntimeException e) {
            System.err.println("Error reporting comment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi báo cáo bình luận: " + e.getMessage()));
        }
    }

    @PostMapping("/{courseId}/report")
    @ResponseBody
    public ResponseEntity<?> reportCourse(@PathVariable Long courseId,
                                        @RequestBody Map<String, String> request,
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

        String reason = request.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lý do báo cáo không được để trống"));
        }

        try {
            // Gọi service để xử lý báo cáo và gửi notification cho admin
            ReportCourseDTO dto = new ReportCourseDTO();
            dto.setCourseId(courseId);
            dto.setUserId(user.getUserId());
            dto.setReason(reason.trim());
            dto.setReportType("REPORT_COURSE");
            reportService.reportCourse(dto);
            return ResponseEntity.ok(Map.of("success", "Đã báo cáo khóa học thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi báo cáo khóa học: " + e.getMessage()));
        }
    }

    // New endpoint to get comments for a specific lesson
    @GetMapping("/{courseId}/lesson/{lessonId}/comments")
    @ResponseBody
    public ResponseEntity<?> getLessonComments(@PathVariable Long courseId,
                                             @PathVariable Long lessonId) {
        try {
            Course course = courseRepo.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));
            
            Lesson lesson = lessonRepo.findById(lessonId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học"));
            
            // Load comments (Rating = null) for the specific lesson only
            List<CourseReview> parentComments = reviewRepo.findByLessonAndRatingIsNullAndParentReviewIsNull(lesson)
                .stream().filter(c -> !c.isHidden())
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())) // MỚI NHẤT LÊN TRÊN
                .collect(Collectors.toList());
            List<CommentDTO> comments = new ArrayList<>();
            
            for (CourseReview parentComment : parentComments) {
                CommentDTO commentDTO = commentMapper.toDTO(parentComment);
                
                // Load replies for this comment
                List<CourseReview> replies = reviewRepo.findByParentReviewOrderByCreatedAtDesc(parentComment)
                    .stream().filter(r -> !r.isHidden())
                    .collect(Collectors.toList());
                List<CommentDTO> replyDTOs = replies.stream()
                        .map(reply -> commentMapper.toDTO(reply))
                        .sorted(Comparator.comparing(CommentDTO::getCreatedAt)) // Đảm bảo reply mới nhất xuống dưới
                        .collect(Collectors.toList());
                commentDTO.setChildren(replyDTOs);
                
                comments.add(commentDTO);
            }
            
            return ResponseEntity.ok(comments);
        } catch (RuntimeException e) {
            System.err.println("Error getting lesson comments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi lấy bình luận: " + e.getMessage()));
        }
    }
} 