package com.OLearning.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDetailDTO {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String courseImg;
    private Double price;
}
