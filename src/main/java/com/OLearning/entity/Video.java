package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="Video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "videoUrl", nullable = false, length = 1000)
    private String videoUrl;
    private LocalDateTime uploadDate;
    private Integer duration;
    @OneToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
}
