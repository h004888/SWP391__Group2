package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "InstructorRequests")
public class InstructorRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RequestID")
    private Long requestId;
    
    @Column(name = "RequestDate")
    private LocalDateTime requestDate;
    
    @Column(name = "Status")
    private String status ;
    
    @Column(name = "Note", columnDefinition = "NVARCHAR(1000)")
    private String note;
    
    @Column(name = "DecisionDate")
    private LocalDateTime decisionDate;

    @Column(name = "Personal_skill", columnDefinition = "NVARCHAR(MAX)")
    private String personalSkill;

    @Column(name = "FileUrl", columnDefinition = "NVARCHAR(1000)")
    private String fileUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UserID", nullable = false)
    private User user; // Người gửi yêu cầu

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "AdminID")
    private User admin; // Người duyệt yêu cầu

}
