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
    private Long lessonId;
    private String title;
    private String description;
    private String contentType = "video";
    private Integer orderNumber;
    private Boolean isFree;
    private Integer duration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "ChapterID")
    private Chapters chapter;

    @OneToMany(mappedBy = "lesson")
    private List<Video> videos;
    @OneToMany(mappedBy = "lesson")
    private List<Quiz> quizzes;

    @OneToMany(mappedBy = "videoLesson")
    private List<Video> listOfVideos;
}
