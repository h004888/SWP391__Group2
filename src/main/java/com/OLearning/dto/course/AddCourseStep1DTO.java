package com.OLearning.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must be less than 500 characters")
    private String title;
    @NotBlank(message = "Description is required")
    private String description;
    @NotBlank(message = "Category is required")
    private String categoryName;
    private Long userId;
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;
    @NotBlank(message = "Course Level is required")
    private String courseLevel; //beginner, intermediate, advanced
}
