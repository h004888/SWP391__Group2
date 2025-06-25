package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.Formula;

@Entity
@Table(name = "Courses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

        @Column(name = "discount")
        private BigDecimal discount;

        @Column(name = "CourseImg")
        private String courseImg;

        @Column(name = "CreatedAt")
        private LocalDateTime createdAt;

        @Column(name = "UpdatedAt")
        private LocalDateTime updatedAt;

        @Column(name = "Status")
        private String status = "pending";

        @Column(name = "canResubmit")
        private Boolean canResubmit;

        @Column(name = "CourseLevel")
        private String courseLevel;

        @Column(name = "VideoUrlPreview", columnDefinition = "nvarchar(max)")
        private String videoUrlPreview;

        @ManyToOne
        @JoinColumn(name = "userId")
        private User instructor;

        @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE, orphanRemoval = true)
        private List<Chapter> listOfChapters;

        @ManyToOne
        @JoinColumn(name = "CategoryID")
        private Category category;

        @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
        private List<Enrollment> enrollments;

        @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
        private List<OrderDetail> orderDetails;

        @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
        private List<CourseReview> courseReviews;

        @Column(name = "IsFree")
        private Boolean isFree;

        public Double getAverageRating() {
                return courseReviews.stream().mapToDouble(CourseReview::getRating).average().orElse(0.0);
        }

        public Long getReviewCount() {
                return (long) courseReviews.size();
        }

        public Integer totalStudentEnrolled() {
                return enrollments.size();
        }

        public Integer getDuration() {
                return listOfChapters.stream()
                                .mapToInt(ch -> ch.getLessons().stream().mapToInt(Lesson::getDuration).sum())
                                .sum();
        }

        public Integer getTotalLessons() {
                return listOfChapters.stream().mapToInt(ch -> ch.getLessons().size()).sum();
        }

}
