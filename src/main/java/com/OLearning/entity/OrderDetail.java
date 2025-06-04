package com.OLearning.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class OrderDetail {
    @ManyToOne
    @JoinColumn(name = "orderId")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;
}
