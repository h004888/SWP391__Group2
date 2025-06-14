package com.OLearning.mapper.cart;

import com.OLearning.dto.cart.CartDetailDTO;
import com.OLearning.entity.CartDetail;
import org.springframework.stereotype.Component;

@Component
public class CartDetailMapper {
    public CartDetailDTO toDTO(CartDetail cartDetail) {
        if (cartDetail == null || cartDetail.getCourse() == null) {
            return null;
        }
        CartDetailDTO dto = new CartDetailDTO();
        dto.setId(cartDetail.getId());
        dto.setCourseId(cartDetail.getCourse().getCourseId());
        dto.setCourseTitle(cartDetail.getCourse().getTitle());
        dto.setCourseImg(cartDetail.getCourse().getCourseImg());
        dto.setPrice(cartDetail.getPrice());
        return dto;
    }
}
