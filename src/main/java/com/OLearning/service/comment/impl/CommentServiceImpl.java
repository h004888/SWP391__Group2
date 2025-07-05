package com.OLearning.service.comment.impl;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Enrollment;
import com.OLearning.entity.Report;
import com.OLearning.entity.User;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.repository.ReportRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    private final CourseReviewRepository reviewRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final ReportRepository reportRepo;

    @Override
    public void addComment(CommentDTO dto, Long userId, Long courseId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));

        // Kiểm tra xem user đã đăng ký khóa học chưa
        Enrollment enrollment = enrollmentRepo.findByUserAndCourse(user, course)
                .orElseThrow(() -> new RuntimeException("Bạn cần đăng ký khóa học để có thể bình luận"));

        CourseReview review = new CourseReview();
        review.setEnrollment(enrollment);
        review.setCourse(course);
        review.setComment(dto.getComment());
        review.setRating(0); // Set default rating for comments
        review.setCreatedAt(LocalDateTime.now());
        reviewRepo.save(review);
        
        System.out.println("Comment added successfully: " + review.getReviewId());
    }

    @Override
    public void replyComment(CommentDTO dto, Long userId, Long courseId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));

        // Kiểm tra xem user đã đăng ký khóa học chưa
        Enrollment enrollment = enrollmentRepo.findByUserAndCourse(user, course)
                .orElseThrow(() -> new RuntimeException("Bạn cần đăng ký khóa học để có thể trả lời bình luận"));

        // Kiểm tra comment cha tồn tại
        CourseReview parentReview = reviewRepo.findById(dto.getParentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận gốc"));

        CourseReview reply = new CourseReview();
        reply.setEnrollment(enrollment);
        reply.setCourse(course);
        reply.setComment(dto.getComment());
        reply.setRating(0); // Set default rating for replies
        reply.setCreatedAt(LocalDateTime.now());
        reply.setParentReview(parentReview);
        reviewRepo.save(reply);
    }

    @Override
    public void editComment(CommentDTO dto, Long userId, Long courseId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        CourseReview review = reviewRepo.findById(dto.getReviewId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

        // Kiểm tra quyền sửa comment
        if (!review.getEnrollment().getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền sửa bình luận này");
        }

        review.setComment(dto.getComment());
        review.setUpdatedAt(LocalDateTime.now());
        reviewRepo.save(review);
    }

    @Override
    public void deleteComment(Long commentId, Long userId, Long courseId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        CourseReview review = reviewRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

        // Kiểm tra quyền xóa comment
        if (!review.getEnrollment().getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa bình luận này");
        }

        // Xóa tất cả các reply của comment này
        reviewRepo.deleteByParentReview(review);
        // Xóa comment
        reviewRepo.delete(review);
    }

    @Override
    public void reportComment(Long commentId, Long userId, Long courseId, String reason) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));
        CourseReview review = reviewRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

        // Kiểm tra xem user đã báo cáo comment này chưa (dựa trên content)
        List<Report> existingReports = reportRepo.findByUser_UserId(userId);
        boolean alreadyReported = existingReports.stream()
                .anyMatch(report -> "COMMENT".equals(report.getReportType()) && 
                        reason.equals(report.getContent()) && 
                        courseId.equals(report.getCourse().getCourseId()));

        if (alreadyReported) {
            throw new RuntimeException("Bạn đã báo cáo bình luận này rồi");
        }

        // Tạo report mới
        Report report = new Report();
        report.setReportType("COMMENT");
        report.setCourse(course);
        report.setUser(user);
        report.setContent(reason);
        report.setCreatedAt(LocalDateTime.now());
        report.setStatus("PENDING");
        reportRepo.save(report);
    }
}


