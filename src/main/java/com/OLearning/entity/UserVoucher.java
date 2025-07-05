package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "voucherId", nullable = false)
    private Voucher voucher;
    @Column(name = "IsUsed")
    private Boolean isUsed;
    @Column(name = "UsedDate")
    private LocalDateTime usedDate;

}
