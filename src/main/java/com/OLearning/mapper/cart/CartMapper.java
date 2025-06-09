package com.OLearning.mapper.cart;

import com.OLearning.dto.cart.CartDTO;
import com.OLearning.entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {
    @Autowired
    private CartDetailMapper cartDetailMapper;

    public CartDTO toDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getCartId());
        dto.setUserId(cart.getUser() != null ? cart.getUser().getUserId() : null);
        dto.setTotal(cart.getTotal());
        dto.setCartDetails(cart.getCartDetails() != null
                ? cart.getCartDetails().stream()
                .map(cartDetailMapper::toDTO)
                .collect(Collectors.toList())
                : null);
        return dto;
    }

    public List<CartDTO> toDTOList(List<Cart> carts) {
        return carts.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }}
