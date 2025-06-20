package com.OLearning.mapper.order;

import com.OLearning.dto.order.OrdersDTO;
import com.OLearning.entity.Order;
import com.OLearning.entity.OrderDetail;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrdersMapper {
    public OrdersDTO toDTO(Order orders) {
        OrdersDTO dto = new OrdersDTO();
        dto.setOrderId(orders.getOrderId());
        dto.setAmount(orders.getAmount());
        dto.setOrderType(orders.getOrderType());
        dto.setStatus(orders.getStatus().toUpperCase());//upperCase status
        dto.setOrderDate(orders.getOrderDate());
        dto.setRefCode(orders.getRefCode());
        dto.setUsername(orders.getUser() != null ? orders.getUser().getUsername() : null);
        dto.setRole(orders.getUser() != null && orders.getUser().getRole() != null
                ? orders.getUser().getRole().getName()
                : null);
        return dto;
    }

    public List<OrdersDTO> toDTOList(List<Order> orders) {
        return orders.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
