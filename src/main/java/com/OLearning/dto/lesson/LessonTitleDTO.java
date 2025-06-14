package com.OLearning.dto.lesson;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LessonTitleDTO {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Description is required")
    private String description;
//    @NotBlank(message = "Content type is required")
//    private String contentType;
    @Min(value = 1, message = "Order number must be at least 1")
    @Digits(integer = 3, fraction = 0, message = "Order number must be numeric")
    private Integer orderNumber;
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 999, message = "Duration cannot exceed 999 minutes")
    @Digits(integer = 3, fraction = 0, message = "Duration must be a whole number")
    private Integer duration;
    private Boolean isFree=false;
    private Long chapterId;
}
