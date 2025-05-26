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
public class Lessons {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonId;
    private String title;
    private String description;
    private String contentType;
    private String content;
    private String duration;
    private Boolean IsFree;
    private LocalDateTime CreatedAt;
    private LocalDateTime UpdatedAt;
    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;
}
