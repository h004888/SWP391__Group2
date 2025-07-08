package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "UserVouchers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "voucherID", nullable = false)
    private Voucher voucher;
    @Column(name = "IsUsed")
    private Boolean isUsed;
    @Column(name = "UsedDate")
    private LocalDate usedDate;

}
