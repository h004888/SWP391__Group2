package com.OLearning.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CoinTransaction")
@Getter
@Setter
public class CoinTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    private BigDecimal amount;
    private String transactionType;
    private String status;
    private String refCode;
    private String note;
    private LocalDateTime createdAt ;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;
}
