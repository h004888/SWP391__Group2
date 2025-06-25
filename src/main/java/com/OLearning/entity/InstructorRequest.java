package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "InstructorRequests")
public class InstructorRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    private LocalDateTime requestDate;
    private String status ;
    @Column(columnDefinition = "NVARCHAR(1000)")
    private String note;
    private LocalDateTime decisionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user; // Người gửi yêu cầu

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adminId")
    private User admin; // Người duyệt yêu cầu

}
