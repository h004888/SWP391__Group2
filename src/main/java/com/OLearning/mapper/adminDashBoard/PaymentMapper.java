package com.OLearning.mapper.adminDashBoard;

import com.OLearning.dto.adminDashBoard.PaymentDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.Payment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {
    public PaymentDTO toDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentType(payment.getPaymenttype());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setNote(payment.getNote());
        dto.setUsername(payment.getUser().getUsername());

        // Lấy danh sách courseNames từ courses của Payment
        Set<Course> courses = payment.getCourses();
        List<String> courseNames = (courses != null)
                ? courses.stream().map(Course::getTitle).collect(Collectors.toList())
                : List.of();
        dto.setCourseNames(courseNames);

        return dto;
    }

    public List<PaymentDTO> toDTOList(List<Payment> payments) {
        return payments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
