package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CoinTransaction")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CoinTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TransactionID")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Column(name = "Amount")
    private BigDecimal amount;

    @Column(name = "TransactionType")
    private String transactionType;

    @Column(name = "Status")
    private String status;

    @Column(name = "Note")
    private String note;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "OrderID", nullable = true)
    private Order order;
} 