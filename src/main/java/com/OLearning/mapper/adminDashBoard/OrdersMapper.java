package com.OLearning.mapper.adminDashBoard;

import com.OLearning.dto.adminDashBoard.OrdersDTO;

import com.OLearning.entity.OrderDetail;
import com.OLearning.entity.Orders;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrdersMapper {
    public OrdersDTO toDTO(Orders orders) {
        OrdersDTO dto = new OrdersDTO();
        dto.setOrderId(orders.getOrderId());
        dto.setAmount(orders.getAmount());
        dto.setOrderType(orders.getOrderType());
        dto.setStatus(orders.getStatus());
        dto.setOrderDate(orders.getOrderDate());
        dto.setNote(orders.getNote());
        dto.setUsername(orders.getUser() != null ? orders.getUser().getUsername() : null);

        // Lấy danh sách courseNames từ orderDetails
        Set<OrderDetail> orderDetails = orders.getOrderDetails();
        List<String> courseNames = (orderDetails != null)
                ? orderDetails.stream()
                .map(orderDetail -> orderDetail.getCourse() != null ? orderDetail.getCourse().getTitle() : null)
                .filter(courseName -> courseName != null)
                .collect(Collectors.toList())
                : List.of();
        dto.setCourseNames(courseNames);

        return dto;
    }

    public List<OrdersDTO> toDTOList(List<Orders> orders) {
        return orders.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
