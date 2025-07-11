package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "Orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID")
    private Long orderId;

    @Column(name = "Amount")
    private double amount;

    @Column(name = "OrderType")
    private String orderType;

    @Column(name = "Status")
    private String status;

    @Column(name = "OrderDate")
    private LocalDateTime orderDate;

    @Column(name = "RefCode")
    private String refCode;

    @Column(name = "Description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<CoinTransaction> coinTransactions;

}