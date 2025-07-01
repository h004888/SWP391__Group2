package com.OLearning.dto.coinTransaction;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoinTransactionDTO {
    private Long transactionId;
    @NotBlank
    private BigDecimal amount;
    private String transactionType;
    private String status;
    private String note;
    private LocalDateTime createdAt;
    private String refCode;
    private String courseName;
}
