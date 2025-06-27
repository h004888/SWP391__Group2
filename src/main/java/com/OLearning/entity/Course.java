package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Courses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;
    private Boolean isFree = false; //step 1
    private String title;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    private Double price;
    private Double discount;
    private String courseImg;
    private String courseLevel; //beginner, intermediate, advanced
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status; //=draft
    private Boolean canResubmit;
    @Column(name = "videoUrlPreview", columnDefinition = "nvarchar(max)")
    private String videoUrlPreview;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User instructor;
    @ManyToOne
    @JoinColumn(name = "CategoryID")
    private Category category;
    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Chapter> listOfChapters;

}

