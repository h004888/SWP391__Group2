package com.OLearning.dto.adminDashBoard;

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
public class PaymentDTO {
    private int paymentId;
    private double amount;
    private String paymentType;
    private String status;
    private LocalDateTime paymentDate;
    private String note;
    private String username;
    private List<String> courseNames;
}
