package com.OLearning.dto.voucher;

import com.OLearning.entity.Voucher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherStatsDTO {
    private Long totalVoucher;
    private Long totalValid;
    private Long totalExpired;
    private List<Voucher> validVouchers;
    private List<Voucher> expiredVouchers;
    private String search;
} 