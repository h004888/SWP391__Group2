package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "OrderDetail")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orderId")
    private Orders order;
    @ManyToOne
    @JoinColumn(name = "CourseId")
    private Course course;

    @Column(name = "UnitPrice")
    private Double unitPrice;

}
