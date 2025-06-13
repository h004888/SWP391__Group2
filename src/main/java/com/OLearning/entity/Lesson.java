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
    private String contentType;
    private Integer orderNumber= 0;
    private Integer duration;
    private Boolean isFree;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "chapterId")
    private Chapter chapter;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL)
    private Video video;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> quizzes;
}
