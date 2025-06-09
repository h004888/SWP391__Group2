package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
@Setter
@Table(name = "Carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartDetail> cartDetails;

    private Long total; // tong so san pham có trong gio hang


}
