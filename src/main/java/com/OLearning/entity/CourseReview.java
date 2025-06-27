package com.OLearning.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CourseReviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReviewID")
    private Long reviewId;

    @OneToOne
    @JoinColumn(name = "EnrollmentID", nullable = false, unique = true)
    private Enrollment enrollment;

    @ManyToOne
    @JoinColumn(name = "CourseID", nullable = false)
    private Course course;



    @Column(name = "Rating", nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    @Column(name = "Comment", length = 1000)
    private boolean hidden = false;
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_review_id")
    private CourseReview parentReview;

    @OneToMany(mappedBy = "parentReview", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<CourseReview> children = new java.util.ArrayList<>();


}
