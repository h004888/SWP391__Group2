package com.OLearning.service.comment;

import com.OLearning.dto.comment.CommentDTO;

public interface CommentService {
    void postComment(CommentDTO dto);
    void updateComment(CommentDTO dto);
    CommentDTO getComment(Long reviewId);


}

