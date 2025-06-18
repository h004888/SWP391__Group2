package com.OLearning.dto.course;

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
    private BigDecimal price;
    private BigDecimal discount;
    private String courseImg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String courseLevel;
    private Double averageRating;
    private Long reviewCount;
    private Integer duration;

}
