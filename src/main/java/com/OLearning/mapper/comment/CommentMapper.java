package com.OLearning.mapper.comment;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.User;
import com.OLearning.entity.Enrollment;
import com.OLearning.entity.Lesson;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public CourseReview toEntity(CommentDTO dto, Enrollment enrollment, Course course, CourseReview parent, Lesson lesson) {
        return CourseReview.builder()
                .enrollment(enrollment)
                .course(course)
                .lesson(lesson)
                .comment(dto.getComment())
                .rating(dto.getRating())
                .parentReview(parent)
                .replyToUserName(dto.getReplyToUserName())
                .build();
    }

    public CommentDTO toDTO(CourseReview review) {
        CommentDTO dto = new CommentDTO();
        dto.setReviewId(review.getReviewId());
        dto.setCourseId(review.getCourse().getCourseId());
        dto.setLessonId(review.getLesson() != null ? review.getLesson().getLessonId() : null);
        if (review.getEnrollment() != null) {
            dto.setUserId(review.getEnrollment().getUser().getUserId());
            dto.setUser(review.getEnrollment().getUser());
        } else if (review.getCourse() != null && review.getCourse().getInstructor() != null) {
            dto.setUserId(review.getCourse().getInstructor().getUserId());
            dto.setUser(review.getCourse().getInstructor());
        } else {
            dto.setUserId(null);
            dto.setUser(null);
        }
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        dto.setParentId(review.getParentReview() != null ? review.getParentReview().getReviewId() : null);
        dto.setReplyToUserName(review.getReplyToUserName());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        dto.setHidden(review.isHidden());
        return dto;
    }
    
    public void updateEntity(CourseReview review, CommentDTO dto, CourseReview parent, Lesson lesson) {
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());
        review.setUpdatedAt(LocalDateTime.now());
        review.setParentReview(parent);
        review.setLesson(lesson);
    }
}

