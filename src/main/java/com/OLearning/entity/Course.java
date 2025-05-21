package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Courses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    private String title;
    private String description;
    private Double price;
    private Double discount;
    private String courseImg;
    private Integer duration;
    private Integer totalLessons;
    private Integer totalRatings;
    private Integer totalStudentEnrolled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isChecked;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User instructor;

//    @ManyToOne
//    @JoinColumn(name = "categoryId")
//    private Category category;
}

