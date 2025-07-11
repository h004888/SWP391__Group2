package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Vouchers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VoucherID")
    private Long voucherId;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User instructor;

    @Column(name = "Code")
    private String code;

    @Column(name = "Discount")
    private Double discount;

    @Column(name = "ExpiryDate")
    private LocalDate expiryDate;

    @Column(name = "Limitation")
    private Long limitation;

    @Column(name = "UsedCount")
    private Long usedCount;

    @Column(name = "IsActive")
    private Boolean isActive;

    @Column(name = "IsGlobal")
    private Boolean isGlobal;

    @Column(name = "CreatedDate")
    private LocalDate createdDate;

    @OneToMany(mappedBy = "voucher")
    private List<VoucherCourse> voucherCourses;

    @OneToMany(mappedBy = "voucher")
    private List<UserVoucher> userVouchers;
}
