package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "Payments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymentId")
    private int paymentId;
    @Column(name = "amount")
    private double amount;
    @Column(name = "paymenttype")
    private String paymenttype;
    @Column(name = "status")
    private String status;
    @Column(name = "paymentDate")
    private LocalDateTime paymentDate;
    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;


    @ManyToMany
    @JoinTable(
            name = "PaymentDetail",
            joinColumns = @JoinColumn(name = "paymentId"),
            inverseJoinColumns = @JoinColumn(name = "courseId")
    )
    private Set<Course> courses = new HashSet<>();

}
