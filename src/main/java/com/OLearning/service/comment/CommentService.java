package com.OLearning.service.comment;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.entity.CourseReview;

public interface CommentService {
    CourseReview addComment(CommentDTO dto, Long userId, Long courseId);
    void replyComment(CommentDTO dto, Long userId, Long courseId);
    void editComment(CommentDTO dto, Long userId, Long courseId);
    void deleteComment(Long reviewId, Long userId);
    void reportComment(Long reviewId, Long userId, String reason);
    void setCommentHidden(Long reviewId, boolean hidden);
}

