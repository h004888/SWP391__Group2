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
@Table(name = "Chapters")
public class Chapters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chapterId;

    @Column(nullable = false, columnDefinition = "nvarchar(255)")
    private String title;

    @Column(columnDefinition = "nvarchar(max)")
    private String description;

    private Integer orderNumber = 0; //stt chuong trong khoa hoc
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    @OneToMany(mappedBy = "chapter")
    private List<Lessons> lessons;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

}