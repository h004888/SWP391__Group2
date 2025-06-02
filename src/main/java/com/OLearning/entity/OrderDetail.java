package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Order_Detail")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OrderDetail {
    @EmbeddedId
    private OrderDetailId Id;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "orderId")
    private Orders orders;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "courseId")
    private Course course;

    @Column(name = "UnitPrice")
    private Double unitPrice;

}
