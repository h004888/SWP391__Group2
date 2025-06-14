package com.OLearning.dto.course;

import com.OLearning.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    private Long courseId;
    private String title;
    private String description;
    private Double price;
    private Double discount;
    private String courseImg;
    private Integer duration;
    private Integer totalLessons;
    private Integer totalRatings;
    private Integer totalStudentEnrolled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}