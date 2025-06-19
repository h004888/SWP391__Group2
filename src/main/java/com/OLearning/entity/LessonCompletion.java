package com.OLearning.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LessonCompletion")
public class LessonCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CompletionID") // Ánh xạ với cột CompletionID
    private int completionID;

    @ManyToOne // Mối quan hệ nhiều-đến-một với User
    @JoinColumn(name = "UserID", nullable = false) // Ánh xạ với khóa ngoại UserID
    private User user; // Đối tượng User liên quan

    @ManyToOne // Mối quan hệ nhiều-đến-một với Lesson
    @JoinColumn(name = "LessonID", nullable = false) // Ánh xạ với khóa ngoại LessonID
    private Lesson lesson; // Đối tượng Lesson liên quan

    @Column(name = "CompletedAt", nullable = false, updatable = false) // Ánh xạ với cột CompletedAt
    @Temporal(TemporalType.TIMESTAMP) // Chỉ định kiểu dữ liệu thời gian
    @CreationTimestamp // Tự động điền thời gian khi tạo bản ghi mới
    private Date completedAt;
}
