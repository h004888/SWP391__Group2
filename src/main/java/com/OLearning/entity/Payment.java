package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.math.BigDecimal;

@Entity
@Table(name = "Payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentID;
    @ManyToOne
    @JoinColumn(name = "userID")
    private User user;
    @ManyToOne
    @JoinColumn(name = "courseID")
    private Course course;
    @Column(name = "amount")
    private double amount;
    @Column(name = "paymentType")
    private String paymentType;
    @Column(name = "status")
    private String status;
    @Column(name = "paymentDate")
    private Date paymentDate;
    @Column(name = "note")
    private String note;
}
