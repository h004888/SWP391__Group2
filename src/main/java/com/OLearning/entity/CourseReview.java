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
    private Long reviewId;

    @OneToOne
    @JoinColumn(name = "enrollmentId", nullable = false, unique = true)
    private Enrollment enrollment;

    @ManyToOne
    @JoinColumn(name = "courseId", nullable = false)
    private Course course;



    @Column(nullable = true)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false)
    private boolean hidden = false;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_review_id")
    private CourseReview parentReview;

    @OneToMany(mappedBy = "parentReview", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<CourseReview> children = new java.util.ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    @Transient
    public User getUser() {
        return enrollment != null ? enrollment.getUser() : null;
    }
}
