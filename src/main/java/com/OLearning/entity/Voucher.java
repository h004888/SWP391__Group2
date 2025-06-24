package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Vouchers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voucherId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User instructor;

    private String code;
    private Double discount;
    private LocalDateTime expiryDate;
    private Long limitation;
    private Long usedCount;
    private Boolean isActive;
    private Boolean isGlobal;
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "voucher")
    private List<VoucherCourse> voucherCourses;

    @OneToMany(mappedBy = "voucher")
    private List<UserVoucher> userVouchers;
}
