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
public class Lessons {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonId;

    @Column(columnDefinition = "nvarchar(255)")
    private String title;

    @Column(columnDefinition = "nvarchar(max)")
    private String description;

    private String contentType;
    private Integer orderNumber= 0;
    private Integer duration;
    private Boolean isFree;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "chapterId")
    private Chapters chapter;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "videoId")
    private Video video;
}