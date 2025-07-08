package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "VoucherCourses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class VoucherCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "voucherID")
    private Voucher voucher;
    @ManyToOne
    @JoinColumn(name = "CourseID")
    private Course course;
}
