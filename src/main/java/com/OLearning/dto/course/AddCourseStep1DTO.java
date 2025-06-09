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
public class AddCourseStep1DTO {
    private Long id;
    private String title;
    private String description;
    private String categoryName;
    private Long userId;
    private MultipartFile courseImg;
}
