package com.OLearning.dto.instructorDashboard;

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
    private Double price;
    private Double discount;
    private String courseImg;
    private Integer duration;
    private Integer totalLessons;
    private LocalDateTime createdAt;
    private String CategoryName;
}
