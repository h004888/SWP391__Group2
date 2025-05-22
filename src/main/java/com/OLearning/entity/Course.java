package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private int courseID;

//    @ManyToOne
//    @JoinColumn(name = "userID")
//    private User user;

//    @ManyToOne
//    @JoinColumn(name = "categoryID")
//    private Category category;

    private String title;
    @Column(length = 2000)
    private String description;
    private BigDecimal price;
    private BigDecimal discount;
    private String courseImg;
    private int duration;
    private int totalLessons;
    private int totalRatings;
    private int totalStudentErolled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Lesson> lessons;
}
