package com.OLearning.dto.course;

import com.OLearning.entity.User;
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
    private String description;
    private Double price;
    private Double discount;
    private String courseImg;
    private Boolean isFree;
    private String categoryName;
    private int totalLessons;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User instructor;
    private String status;
    private String courseLevel; //beginner, intermediate, advanced
    private int totalStudentEnrolled;
}
