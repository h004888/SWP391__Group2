package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Fees")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Fees {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feeId;
    private Long MinEnrollments;
    private Long MaxEnrollments;
    private Long MaintenanceFee;

    @OneToMany(mappedBy = "fee", fetch = FetchType.LAZY)
    private List<CourseMaintenance> courseMaintenances;
}
