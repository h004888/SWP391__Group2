package com.OLearning.controller.homePage;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Report;
import com.OLearning.entity.User;
import com.OLearning.mapper.comment.CommentMapper;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.repository.ReportRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @PostMapping("/{courseId}/comment")
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

    @PutMapping("/{courseId}/comment/{commentId}/edit")
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
            commentService.deleteComment(commentId, user.getUserId(), courseId);
            return ResponseEntity.ok(Map.of("success", "Đã xóa bình luận thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi xóa bình luận: " + e.getMessage()));
        }
    }

    @GetMapping("/{courseId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsForCourse(@PathVariable Long courseId) {
        try {
            Course course = courseRepo.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));

            // Lấy danh sách parent comments
            List<CourseReview> parentComments = reviewRepo.findByCourseAndParentReviewIsNull(course);
            List<CommentDTO> comments = new ArrayList<>();

            for (CourseReview parentComment : parentComments) {
                CommentDTO commentDTO = commentMapper.toDTO(parentComment);

                // Load replies for this comment
                List<CourseReview> replies = reviewRepo.findByParentReviewOrderByCreatedAtDesc(parentComment);
                List<CommentDTO> replyDTOs = replies.stream()
                        .map(reply -> commentMapper.toDTO(reply))
                        .collect(Collectors.toList());
                commentDTO.setChildren(replyDTOs);

                comments.add(commentDTO);
            }

            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
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
            commentService.reportComment(commentId, user.getUserId(), courseId, reason.trim());
            return ResponseEntity.ok(Map.of("success", "Đã báo cáo bình luận thành công"));
        } catch (RuntimeException e) {
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
            // Tạo report cho course
            Report report = new Report();
            report.setReportType("BLOCK_COURSE");
            report.setCourse(courseRepo.findById(courseId).orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học")));
            report.setUser(user);
            report.setContent(reason.trim());
            report.setCreatedAt(java.time.LocalDateTime.now());
            report.setStatus("PENDING");
            reportRepo.save(report);
            
            return ResponseEntity.ok(Map.of("success", "Đã báo cáo khóa học thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi báo cáo khóa học: " + e.getMessage()));
        }
    }
} 