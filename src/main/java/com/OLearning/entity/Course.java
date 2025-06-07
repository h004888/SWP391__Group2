package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
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
    private String status;
    private Boolean canResubmit;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User instructor;

    @ManyToOne
    @JoinColumn(name = "CategoryID")
    private Category category;

//    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
//    private List<Lessons> listOfLessons;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;


}

