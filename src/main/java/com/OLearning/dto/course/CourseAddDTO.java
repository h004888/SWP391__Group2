package com.OLearning.dto.course;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseAddDTO {
    private String title;
    private String description;
    private Double price;
    private Double discount;
    private String categoryName;
    private Long userId;
    private MultipartFile courseImg;
}
