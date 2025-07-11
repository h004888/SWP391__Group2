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
public class Fees {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feeId;
    private Long minEnrollments;
    private Long maxEnrollments;
    private Long maintenanceFee;

    @OneToMany(mappedBy = "fee", fetch = FetchType.LAZY)
    private List<CourseMaintenance> courseMaintenances;
}
