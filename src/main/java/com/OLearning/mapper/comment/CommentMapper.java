package com.OLearning.mapper.comment;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public CourseReview toEntity(CommentDTO dto, User user, Course course) {
        CourseReview review = new CourseReview();
        review.setCourse(course);
        review.setUser(user);
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }
}
