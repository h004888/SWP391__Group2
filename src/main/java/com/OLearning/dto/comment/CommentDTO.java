package com.OLearning.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CommentDTO {
    private Long reviewId;
    private Long courseId;
    private Long userId;
    private String comment;
    private Integer rating;
}


