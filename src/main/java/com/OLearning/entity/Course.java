package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CourseID")
    private Long courseId;

    @Column(name = "Title")
    private String title;

    @Column(name = "Description")
    private String description;

    @Column(name = "Price")
    private BigDecimal price;

    @Column(name = "Discount")
    private BigDecimal discount;

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

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "Status")
    private String status = "pending";

    @Column(name = "CanResubmit")
    private Boolean canResubmit;
    @Column(name = "CourseLevel")
    private String courseLevel; 

    @ManyToOne
    @JoinColumn(name = "UserID")
    private User instructor;    

    @ManyToOne
    @JoinColumn(name = "CategoryID")
    private Category category;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Lesson> listOfLessons;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Chapters> chapters;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;

}
