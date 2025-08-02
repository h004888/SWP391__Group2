package com.OLearning.mapper.order;

import com.OLearning.dto.order.OrderHistoryDTO;
import com.OLearning.entity.Order;
import com.OLearning.entity.OrderDetail;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderHistoryMapper {

    public List<OrderHistoryDTO> toDTOList(Order order) {
        if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
            return List.of();
        }

        return order.getOrderDetails().stream()
                .map(detail -> toDTO(order, detail))
                .collect(Collectors.toList());
    }

    public OrderHistoryDTO toDTO(Order order, OrderDetail detail) {
        OrderHistoryDTO dto = new OrderHistoryDTO();
        dto.setOrderId(order.getOrderId());
        dto.setAmount(order.getAmount());
        dto.setOrderType(order.getOrderType());
        dto.setStatus(order.getStatus());
        dto.setDescription(order.getDescription());
        dto.setOrderDate(order.getOrderDate());
        dto.setRefCode(order.getRefCode());

        // Lấy thông tin từ OrderDetail cụ thể
        if (detail.getCourse() != null) {
            dto.setCourseName(detail.getCourse().getTitle());
            dto.setInstructorName(detail.getCourse().getInstructor() != null ?
                    detail.getCourse().getInstructor().getFullName() : null);
            dto.setOriginalPrice(detail.getCourse().getPrice());
            dto.setDiscountedPrice(detail.getUnitPrice());
            if (detail.getVoucher() != null) {
                dto.setVoucherDiscount(detail.getVoucher().getDiscount());
            } else {
                dto.setVoucherDiscount(0.0);
            }
        }

        // Xác định payment method
        dto.setPaymentMethod(determinePaymentMethod(order));

        return dto;
    }

    public List<OrderHistoryDTO> toDTOList(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> toDTOList(order).stream())
                .collect(Collectors.toList());
    }

    private String determinePaymentMethod(Order order) {
        String description = order.getDescription();
        if (description != null) {
            if (description.contains("VNPay") || description.contains("vnp_")) {
                return "VNPay";
            } else if (description.contains("QR") || description.contains("qr")) {
                return "QR Banking";
            }
        }

        String refCode = order.getRefCode();
        if (refCode != null) {
            if (refCode.matches("\\d{10,}")) {
                return "VNPay";
            }
        }

        String orderType = order.getOrderType();
        if ("course_purchase".equals(orderType)) {
            return "Ví nội bộ";
        }

        return "Ví nội bộ";
    }
} 