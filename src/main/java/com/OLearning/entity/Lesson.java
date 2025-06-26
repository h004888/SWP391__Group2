package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Lessons")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LessonID")
    private Long lessonId;
    
    @Column(name = "Title")
    private String title;
    
    @Column(name = "Description")
    private String description;
    
    @Column(name = "ContentType")
    private String contentType;
    
    @Column(name = "OrderNumber")
    private Integer orderNumber= 0;
    
    @Column(name = "Duration")
    private Integer duration;
    
    @Column(name = "IsFree")
    private Boolean isFree;
    
    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;
    
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ChapterID", referencedColumnName = "chapterId")
    private Chapter chapter;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Video video;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Quiz quiz;
}
