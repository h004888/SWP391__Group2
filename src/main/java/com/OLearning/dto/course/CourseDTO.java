package com.OLearning.dto.course;

import com.OLearning.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CourseDTO {
    private Long courseId;
    private String title;
    private String description;
    private Double price;
    private Double discount;
    private String courseImg;
    private Integer duration;
    private Boolean isFree;
    private String categoryName;
    private Integer totalLessons;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String courseLevel;
    private Double averageRating;
    private Long reviewCount;
    private Integer totalStudentEnrolled;
    private String status;
}

