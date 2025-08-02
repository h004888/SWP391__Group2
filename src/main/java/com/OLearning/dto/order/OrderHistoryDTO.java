package com.OLearning.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryDTO {
    private Long orderId;
    private Double amount;
    private String orderType;
    private String status;
    private String description;
    private LocalDateTime orderDate;
    private String refCode;
    private String courseName;
    private String instructorName;
    private String courseDuration;
    private Double originalPrice;
    private Double discountedPrice;
    private String paymentMethod;
    private Double voucherDiscount;
} 