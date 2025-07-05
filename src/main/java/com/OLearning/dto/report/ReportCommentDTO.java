package com.OLearning.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportCommentDTO {
    private Long commentId;
    private Long courseId;
    private Long userId;
    private String reason;
}
