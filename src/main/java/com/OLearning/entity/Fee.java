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
public class Fee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FeeID")
    private Long feeId;
    
    @Column(name = "MinEnrollments")
    private Long minEnrollments;
    
    @Column(name = "MaxEnrollments")
    private Long maxEnrollments;
    
    @Column(name = "MaintenanceFee")
    private Long maintenanceFee;

    @OneToMany(mappedBy = "fee", fetch = FetchType.LAZY)
    private List<CourseMaintenance> courseMaintenances;
}
