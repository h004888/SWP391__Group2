package com.OLearning.dto.voucher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {
    private Long voucherId;
    private String code;
    private Double discount;
    private LocalDateTime expiryDate;
    private Long limitation;
    private Long usedCount;
    private Boolean isActive;
    private Boolean isGlobal;
    private LocalDateTime createdDate;
    private List<Long> courseName;
}
