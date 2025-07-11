package com.OLearning.dto.enrollment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCourseProgressDTO {
    private Long userId;
    private Long courseId;
    private int enrollmentId;
    private Double progress;
}
