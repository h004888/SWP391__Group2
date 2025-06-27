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
    private final UserRepository userRepo;

    @Override
    public void postComment(CommentDTO dto) {
        Course course = courseRepo.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Kiểm tra xem user có phải là instructor không
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Enrollment enrollment;
        
        // Nếu là instructor, tạo enrollment nếu chưa có
        if (course.getInstructor().getUserId().equals(user.getUserId())) {
            enrollment = enrollmentRepo.findByUser_UserIdAndCourse_CourseId(user.getUserId(), course.getCourseId())
                    .orElseGet(() -> {
                        // Tạo enrollment cho instructor nếu chưa có
                        Enrollment newEnrollment = Enrollment.builder()
                                .user(user)
                                .course(course)
                                .enrollmentDate(new java.util.Date())
                                .progress(Double.parseDouble("100.00"))
                                .status("completed")
                                .build();
                        return enrollmentRepo.save(newEnrollment);
                    });
        } else {
            // Nếu không phải instructor, kiểm tra enrollment bình thường
            enrollment = enrollmentRepo.findByUser_UserIdAndCourse_CourseId(dto.getUserId(), dto.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Bạn chưa tham gia khóa học này."));
        }

        CourseReview parent = null;
        if (dto.getParentId() != null) {
            parent = reviewRepo.findById(dto.getParentId()).orElse(null);
        }

        CourseReview review = mapper.toEntity(dto, enrollment, course, parent);
        reviewRepo.save(review);

        // Gửi notification đến instructor (chỉ khi không phải instructor comment)
        if (!course.getInstructor().getUserId().equals(user.getUserId())) {
            Notification notification = new Notification();
            
            // Tạo message với comment preview
            String commentPreview = dto.getComment();
            if (commentPreview != null && commentPreview.length() > 50) {
                commentPreview = commentPreview.substring(0, 50) + "...";
            }
            
            notification.setMessage("You have a new comment in the course: " + course.getTitle() + " - Comment: " + commentPreview);
            notification.setSentAt(LocalDateTime.now());
            notification.setStatus("failed");
            notification.setType("comment");
            notification.setCourse(course);
            notification.setUser(course.getInstructor());
            notification.setCommentId(review.getReviewId()); // Set comment ID for reply functionality
            notificationRepo.save(notification);
        }
    }

    @Override
    public void updateComment(CommentDTO dto) {
        CourseReview review = reviewRepo.findById(dto.getReviewId())
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Kiểm tra xem user có quyền chỉnh sửa comment này không
        if (!review.getEnrollment().getUser().getUserId().equals(dto.getUserId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa comment này.");
        }

        CourseReview parent = null;
        if (dto.getParentId() != null) {
            parent = reviewRepo.findById(dto.getParentId()).orElse(null);
        }

        mapper.updateEntity(review, dto, parent);
        reviewRepo.save(review);
    }

    @Override
    public CommentDTO getComment(Long reviewId) {
        CourseReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        return mapper.toDTO(review);
    }


}


