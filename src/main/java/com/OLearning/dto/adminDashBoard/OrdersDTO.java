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
public class OrdersDTO {
    private int orderId;
    private double amount;
    private String orderType;
    private String status;
    private LocalDateTime orderDate;
    private String note;
    private String username;
    private List<String> courseNames;
}
