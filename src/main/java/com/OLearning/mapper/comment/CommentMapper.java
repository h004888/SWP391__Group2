package com.OLearning.mapper.comment;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.User;
import com.OLearning.entity.Enrollment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public CourseReview toEntity(CommentDTO dto, Enrollment enrollment, Course course, CourseReview parent) {
        return CourseReview.builder()
                .enrollment(enrollment)
                .course(course)
                .comment(dto.getComment())
                .rating(dto.getRating())
                .parentReview(parent)
                .build();
    }

    public CommentDTO toDTO(CourseReview review) {
        CommentDTO dto = new CommentDTO();
        dto.setReviewId(review.getReviewId());
        dto.setCourseId(review.getCourse().getCourseId());
        dto.setUserId(review.getEnrollment().getUser().getUserId());
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        dto.setParentId(review.getParentReview() != null ? review.getParentReview().getReviewId() : null);
        
        // Use the getUser() method from entity
        dto.setUser(review.getUser());
        
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
    
    public void updateEntity(CourseReview review, CommentDTO dto, CourseReview parent) {
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());
        review.setUpdatedAt(LocalDateTime.now());
        review.setParentReview(parent);
    }
}

