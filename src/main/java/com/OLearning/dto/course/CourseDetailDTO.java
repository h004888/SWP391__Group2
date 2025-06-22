package com.OLearning.dto.course;

import com.OLearning.entity.Category;
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
public class CourseDetailDTO {

    private Long courseId;

    private String title;
    private String description;
    private Double price;
    private Double discount;
    private String courseImg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isChecked;
    private User instructor;
    private Category category;
}