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
@ToString
public class CourseMaintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maintenanceId;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "feeId")
    private Fee fee;

    @ManyToOne
    @JoinColumn(name = "orderId")
    private Order order;

    private LocalDate monthYear;
    private Long enrollmentCount;
    private String status;

    private LocalDate dueDate;
    private LocalDateTime sentAt;
}
