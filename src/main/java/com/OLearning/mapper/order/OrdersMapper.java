package com.OLearning.mapper.order;

import com.OLearning.dto.order.OrdersDTO;
import com.OLearning.entity.Order;
import com.OLearning.entity.OrderDetail;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrdersMapper {
    public OrdersDTO toDTO(Order order) {
        OrdersDTO dto = new OrdersDTO();
        dto.setOrderId(order.getOrderId());
        dto.setAmount(order.getAmount());
        dto.setOrderType(order.getOrderType());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());
        dto.setNote(order.getNote());
        dto.setUsername(order.getUser() != null ? order.getUser().getUsername() : null);

        // Lấy danh sách courseNames từ orderDetails
        Set<OrderDetail> orderDetails = order.getOrderDetails();
        List<String> courseNames = (orderDetails != null)
                ? orderDetails.stream()
                .map(orderDetail -> orderDetail.getCourse() != null ? orderDetail.getCourse().getTitle() : null)
                .filter(courseName -> courseName != null)
                .collect(Collectors.toList())
                : List.of();
        dto.setCourseNames(courseNames);

        return dto;
    }

    public List<OrdersDTO> toDTOList(List<Order> orders) {
        return orders.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
