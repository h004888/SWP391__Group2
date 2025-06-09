package com.OLearning.mapper.cart;

import com.OLearning.dto.cart.CartDetailDTO;
import com.OLearning.entity.CartDetail;
import org.springframework.stereotype.Component;

@Component
public class CartDetailMapper {
    public CartDetailDTO toDTO(CartDetail cartDetail) {
        CartDetailDTO dto = new CartDetailDTO();
        dto.setId(cartDetail.getId());
        dto.setCartId(cartDetail.getCart() != null ? cartDetail.getCart().getCartId() : null);
        dto.setCourseId(cartDetail.getCourse() != null ? cartDetail.getCourse().getCourseId() : null);
        dto.setPrice(cartDetail.getPrice());
        return dto;
    }
}
