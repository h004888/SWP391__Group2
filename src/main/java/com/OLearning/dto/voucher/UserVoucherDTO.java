package com.OLearning.dto.voucher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserVoucherDTO {
    private Long id;
    private Long voucherId;
    private String voucherCode;
    private Double discount;
    private LocalDate expiryDate;
    private Boolean isUsed;
    private LocalDate usedDate;
}
