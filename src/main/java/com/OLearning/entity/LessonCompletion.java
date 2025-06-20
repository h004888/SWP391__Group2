package com.OLearning.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "LessonCompletion",
        uniqueConstraints = @UniqueConstraint(name = "UQ_User_Lesson", columnNames = {"UserID", "LessonID"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CompletionID")
    private Integer completionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    @JsonIgnoreProperties("LessonCompletion")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LessonID", nullable = false)
    @JsonIgnoreProperties("LessonCompletion")
    private Lesson lesson;

    @Column(name = "CompletedAt", nullable = false, updatable = false)
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        this.completedAt = LocalDateTime.now();
    }
}
