package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

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
    private Chapter chapter;

    // Danh sách video thuộc bài học
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<Video> videos;

    // Danh sách quiz thuộc bài học
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<Quiz> quizzes;

    // Danh sách người dùng đã hoàn thành bài học này
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<LessonCompletion> completions;
}
