package com.OLearning.entity;

import java.io.Serializable;
import java.util.Objects;

public class OrderDetailId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;

    private Long courseId;

    // Constructors
    public OrderDetailId() {}

    public OrderDetailId(Long orderId, Long courseId) {
        this.orderId = orderId;
        this.courseId = courseId;
    }

    // Getters and setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    // Equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetailId that = (OrderDetailId) o;
        return Objects.equals(orderId, that.orderId) &&
                Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, courseId);
    }
}