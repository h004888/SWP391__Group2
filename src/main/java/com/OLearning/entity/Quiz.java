package com.OLearning.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class Quiz {
    private Long quizId;
    @ManyToOne
    @JoinColumn(name = "LessonID")
    private Lesson lesson;
}
