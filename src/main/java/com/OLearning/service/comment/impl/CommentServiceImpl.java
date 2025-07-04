package com.OLearning.service.comment.impl;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Enrollment;
import com.OLearning.entity.User;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    private final CourseReviewRepository reviewRepo;
    private final EnrollmentRepository enrollmentRepo;

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
        review.setComment(dto.getComment());
        review.setCreatedAt(LocalDateTime.now());
        reviewRepo.save(review);
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
        reply.setComment(dto.getComment());
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
}


