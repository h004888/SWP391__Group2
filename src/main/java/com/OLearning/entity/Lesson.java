package com.OLearning.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Chapter chapter;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL)
    private Video video;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL)
    private Quiz quiz;
    @ManyToOne
    @JoinColumn(name = "CourseID")
    private Course course;

    // Danh sách người dùng đã hoàn thành bài học này
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<LessonCompletion> completions;
}
