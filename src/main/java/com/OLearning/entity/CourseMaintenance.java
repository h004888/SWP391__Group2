package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "CourseMaintenance")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CourseMaintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaintenanceID")
    private Long maintenanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CourseID")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FeeID")
    private Fee fee;

    @Column(name = "MonthYear")
    private LocalDate monthYear;
    
    @Column(name = "EnrollmentCount")
    private Long enrollmentCount;
    
    @Column(name = "Status")
    private String status;

    @Column(name = "DueDate")
    private LocalDate dueDate;
    
    @Column(name = "SentAt")
    private LocalDateTime sentAt;
}
