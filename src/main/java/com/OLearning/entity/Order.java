package com.OLearning.entity;

import java.util.List;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

public class Order {
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;
    @OneToMany(mappedBy = "order")
    private List<Enrollment> enrollments;
}
