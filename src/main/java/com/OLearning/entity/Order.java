package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Table(name = "Orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private double amount;
    private String orderType;
    private String status;
    private LocalDateTime orderDate;
    private String note;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OrderDetail> orderDetails;


}