package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private String contentType = "video";

    @Column(name = "OrderNumber")
    private Integer orderNumber;

    @Column(name = "IsFree")
    private Boolean isFree;

    @Column(name = "Duration")
    private Integer duration;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "ChapterID")
    private Chapters chapter;

    // XÓA: Course course;

    @OneToMany(mappedBy = "lesson")
    private List<Video> videos;

    @OneToMany(mappedBy = "lesson")
    private List<Quiz> quizzes;

    @OneToMany(mappedBy = "videoLesson")
    private List<Video> listOfVideos;
}
