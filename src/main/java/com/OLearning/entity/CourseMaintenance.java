package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "CourseMaintenance")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CourseMaintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maintenanceId;
    private LocalDate monthYear;
    private Long enrollmentCount;
    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "feeId")
    private Fees fee;
}
