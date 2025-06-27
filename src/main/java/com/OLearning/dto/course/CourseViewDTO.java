package com.OLearning.dto.course;

import java.time.LocalDateTime;
import java.util.List;

import com.OLearning.entity.Category;
import com.OLearning.entity.Chapter;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Enrollment;
import com.OLearning.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CourseViewDTO {

    private Long courseId;
    private Boolean isFree = false;
    private String title;
    private String description;
    private Double price;
    private Double discount;
    private String courseImg;
    private String courseLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    private Boolean canResubmit;
    private String videoUrlPreview;

    private User instructor;
    private Category category;
    private List<Chapter> listOfChapters;
    private List<Enrollment> enrollments;

    private List<CourseReview> courseReviews;
    private Double averageRating;
    private Integer duration;
    private Long totalLessons;
}
