package com.OLearning.mapper.cart;

import com.OLearning.dto.cart.CartDTO;
import com.OLearning.entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CartMapper {
    @Autowired
    private CartDetailMapper cartDetailMapper;

    public CartDTO toDTO(Cart cart) {
        if (cart == null) {
            return new CartDTO(null, null, Collections.emptyList(), 0L);
        }

        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getCartId());
        dto.setUserId(cart.getUser() != null ? cart.getUser().getUserId() : null);
        dto.setTotal(cart.getTotal());

        // Fixed: Check if cartDetails is not null before streaming
        if (cart.getCartDetails() != null) {
            dto.setCartDetails(cart.getCartDetails().stream()
                    .map(cartDetailMapper::toDTO)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        } else {
            dto.setCartDetails(Collections.emptyList());
        }

        return dto;
    }

}
