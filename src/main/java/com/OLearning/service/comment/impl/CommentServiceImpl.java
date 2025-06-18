package com.OLearning.service.comment.impl;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.entity.*;
import com.OLearning.mapper.comment.CommentMapper;
import com.OLearning.repository.*;
import com.OLearning.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CourseReviewRepository reviewRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final CourseRepository courseRepo;
    private final NotificationRepository notificationRepo;
    private final CommentMapper mapper;

    @Override
    public void postComment(CommentDTO dto) {
        Enrollment enrollment = enrollmentRepo.findByUser_UserIdAndCourse_CourseId(dto.getUserId(), dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Bạn chưa tham gia khóa học này."));

//        if (reviewRepo.findByEnrollment(enrollment).isPresent()) {
//            throw new RuntimeException("Bạn đã bình luận trước đó.");
//        }

        Course course = courseRepo.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        CourseReview review = mapper.toEntity(dto, enrollment, course);
        reviewRepo.save(review);

        // Gửi notification đến instructor
        Notification notification = new Notification();
        notification.setMessage("Bạn có một bình luận mới trong khóa học: " + course.getTitle());
        notification.setSentAt(LocalDateTime.now());
        notification.setStatus("failed");
        notification.setType("comment");
        notification.setCourse(course);
        notification.setUser(course.getInstructor());
        notificationRepo.save(notification);
    }

    @Override
    public void updateComment(CommentDTO dto) {
        CourseReview review = reviewRepo.findById(dto.getReviewId())
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Kiểm tra xem user có quyền chỉnh sửa comment này không
        if (!review.getEnrollment().getUser().getUserId().equals(dto.getUserId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa comment này.");
        }

        mapper.updateEntity(review, dto);
        reviewRepo.save(review);
    }

    @Override
    public CommentDTO getComment(Long reviewId) {
        CourseReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        return mapper.toDTO(review);
    }


}


