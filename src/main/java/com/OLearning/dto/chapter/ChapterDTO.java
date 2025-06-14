package com.OLearning.dto.chapter;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChapterDTO {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Description is required")
    private String description;
    @Min(value = 1, message = "Order number must be at least 1")
    @Digits(integer = 3, fraction = 0, message = "Order number must be numeric")
    private Integer orderNumberChapter;
    private Long courseId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderNumberChapter() {
        return orderNumberChapter;
    }

    public void setOrderNumberChapter(Integer ordernNumber) {
        this.orderNumberChapter = ordernNumber;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
