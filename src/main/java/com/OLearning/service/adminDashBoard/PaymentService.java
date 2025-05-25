package com.OLearning.service.adminDashBoard;

import com.OLearning.dto.adminDashBoard.PaymentDTO;
import com.OLearning.entity.Payment;

import com.OLearning.repository.adminDashBoard.PaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class PaymentService {
    @Autowired
    private PaymentRepo paymentRepository;

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return getAllPayments();
        }
        return paymentRepository.findByUserUsernameContaining(username).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> findByCourseName(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            return getAllPayments();
        }
        String pattern = courseName + "__";
        return paymentRepository.findByCourseNamePattern(pattern).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> sortByAmount(String direction) {
        List<Payment> payments;
        if ("asc".equalsIgnoreCase(direction)) {
            payments = paymentRepository.findAllByOrderByAmountAsc();
        } else if ("desc".equalsIgnoreCase(direction)) {
            payments = paymentRepository.findAllByOrderByAmountDesc();
        } else {
            payments = paymentRepository.findAll(); // All
        }
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> sortByDate(String direction) {
        List<Payment> payments;
        if ("asc".equalsIgnoreCase(direction)) {
            payments = paymentRepository.findAllByOrderByPaymentDateAsc();
        } else if ("desc".equalsIgnoreCase(direction)) {
            payments = paymentRepository.findAllByOrderByPaymentDateDesc();
        } else {
            payments = paymentRepository.findAll(); // All
        }
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentType(payment.getPaymenttype());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setNote(payment.getNote());
        dto.setUsername(payment.getUser().getUsername());

        List<String> courseNames = payment.getCourses().stream()
                .map(course -> course.getTitle())
                .collect(Collectors.toList());
        dto.setCourseNames(courseNames);

        return dto;
    }
}
