package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Chapters")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapterID")
    private Long chapterId;
    @Column(nullable = false)
    private String title;
    private String description;
    @Column(name = "orderNumber")
    private Integer orderNumber = 0; //stt chuong trong khoa hoc
    @Column(name = "createdAt")
    private LocalDateTime createdAt;
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "chapter", fetch = FetchType.LAZY)
    private List<Lesson> lessons;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;


}
