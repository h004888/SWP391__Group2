package com.OLearning.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reportType; // BLOCK_COURSE, COMMENT, VIOLATION, INSTRUCTOR_RESPONSE, ...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CourseID")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NotificationID")
    private Notification notification;

    @Column(name = "CommentID")
    private Long commentId;

    @Column(columnDefinition = "TEXT")
    private String content; // Nội dung report/phản hồi

    private String evidenceLink; // Link cloud ảnh/video

    @Column(nullable = false)
    private String status; // pending, processed, rejected, approved...

    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    // Getter, Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Notification getNotification() { return notification; }
    public void setNotification(Notification notification) { this.notification = notification; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getEvidenceLink() { return evidenceLink; }
    public void setEvidenceLink(String evidenceLink) { this.evidenceLink = evidenceLink; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }
} 