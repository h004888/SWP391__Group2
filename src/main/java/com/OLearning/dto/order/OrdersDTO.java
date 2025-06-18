package com.OLearning.dto.order;

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
public class OrdersDTO {
    private Long orderId;
    private double amount;
    private String orderType;
    private String status;
    private LocalDateTime orderDate;
    private String refCode;
    private String username;
    private String role;
}
