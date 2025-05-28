package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    @Column(name = "CourseId")
    private Long courseId;

    @Column(name = "Title")
    private String title;

    @Column(name = "Description")
    private String description;

    @Column(name = "Price")
    private Double price;

    @Column(name = "Discount")
    private Double discount;

    @Column(name = "CourseImg")
    private String courseImg;

    @Column(name = "Duration")
    private Integer duration;

    @Column(name = "TotalLessons")
    private Integer totalLessons;

    @Column(name = "TotalRatings")
    private Integer totalRatings;

    @Column(name = "TotalStudentEnrolled")
    private Integer totalStudentEnrolled;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "isChecked")
    private Boolean isChecked;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User instructor;

//    @ManyToOne
//    @JoinColumn(name = "categoryId")
//    private Category category;
@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private Set<OrderDetail> orderDetails;


}

