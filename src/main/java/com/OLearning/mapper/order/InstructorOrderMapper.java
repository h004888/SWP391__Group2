package com.OLearning.mapper.order;

import com.OLearning.dto.order.InstructorOrderDTO;
import com.OLearning.entity.Order;
import com.OLearning.entity.OrderDetail;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InstructorOrderMapper {
    
    public InstructorOrderDTO toInstructorDTO(Order order, Long instructorId) {
        InstructorOrderDTO dto = new InstructorOrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setOrderDate(order.getOrderDate());
        dto.setOrderType(order.getOrderType());
        dto.setUsername(order.getUser() != null ? order.getUser().getUsername() : null);
        dto.setStatus(order.getStatus());
        
        // Calculate instructor-specific amount and course count
        double instructorAmount = 0.0;
        int instructorCourseCount = 0;
        
        if (order.getOrderDetails() != null) {
            for (OrderDetail detail : order.getOrderDetails()) {
                if (detail.getCourse() != null && 
                    detail.getCourse().getInstructor() != null && 
                    detail.getCourse().getInstructor().getUserId().equals(instructorId)) {
                    
                    // Calculate final price for this course with null safety
                    double unitPrice = detail.getUnitPrice() != null ? detail.getUnitPrice() : 0.0;
                    double discount = detail.getCourse().getDiscount() != null ? detail.getCourse().getDiscount() : 0.0;
                    double finalPrice = unitPrice * (1 - discount / 100.0);
                    
                    instructorAmount += finalPrice;
                    instructorCourseCount++;
                }
            }
        }
        
        dto.setInstructorAmount(instructorAmount);
        dto.setInstructorCourseCount(instructorCourseCount);
        
        return dto;
    }
    
    public List<InstructorOrderDTO> toInstructorDTOList(List<Order> orders, Long instructorId) {
        return orders.stream()
                .map(order -> toInstructorDTO(order, instructorId))
                .collect(Collectors.toList());
    }
} 