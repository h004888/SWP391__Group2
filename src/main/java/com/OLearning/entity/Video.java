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
    @Column(name = "VideoID")
    private Long id;
    
    @Column(name = "VideoURL", nullable = false, length = 1000 ,columnDefinition ="nvarchar(max)")
    private String videoUrl;
    
    @Column(name = "UploadDate")
    private LocalDateTime uploadDate;
    
    @Column(name = "Duration")
    private Integer duration;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LessonID")
    private Lesson lesson;
}
