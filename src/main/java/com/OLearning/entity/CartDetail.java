package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
@Setter
@Table(name = "CartDetail")
public class CartDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double price;
    @ManyToOne
    @JoinColumn(name = "cartId", nullable = false)
    private Cart cart;
    @ManyToOne
    @JoinColumn(name = "courseId", nullable = false)
    private Course course;
}
