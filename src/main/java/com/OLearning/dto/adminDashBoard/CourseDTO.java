package com.OLearning.dto.adminDashBoard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    private Long courseId;
    private String title;
    private Integer duration;
    private Integer totalStudentEnrolled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isChecked;
}
