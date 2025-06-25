package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;



@Entity
@Table(name = "Quiz_test")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QuizID")
    private Long quizId;

    @Column(name = "CreatedAt", columnDefinition = "datetime2(6)")
    private LocalDateTime createdAt;

    @Column(name = "Description", length = 255)
    private String description;

    @Column(name = "TimeLimit")
    private Integer timeLimit;

    @Column(name = "Title", length = 255)
    private String title;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "LessonID", referencedColumnName = "LessonID")
    private Lesson lesson;

}
