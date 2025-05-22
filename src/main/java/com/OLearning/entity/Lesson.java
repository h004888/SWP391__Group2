package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Lessons")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long lessonId;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;
    private String title;
    private String description;
    private String contentType;
    @Column(length = 1000)
    private String content;
    private int duration;
    private boolean isFree;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
