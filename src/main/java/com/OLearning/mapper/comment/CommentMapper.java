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
    public CourseReview toEntity(CommentDTO dto, Enrollment enrollment, Course course) {
        return CourseReview.builder()
                .enrollment(enrollment)
                .course(course)
                .comment(dto.getComment())
                .rating(dto.getRating())
                .build();
    }

    public CommentDTO toDTO(CourseReview review) {
        CommentDTO dto = new CommentDTO();
        dto.setReviewId(review.getReviewId());
        dto.setCourseId(review.getCourse().getCourseId());
        dto.setUserId(review.getEnrollment().getUser().getUserId());
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        return dto;
    }
    
    public void updateEntity(CourseReview review, CommentDTO dto) {
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());
        review.setUpdatedAt(LocalDateTime.now());
    }
}

