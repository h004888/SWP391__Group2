package com.OLearning.service.adminDashBoard;

import com.OLearning.entity.Payment;
import com.OLearning.entity.User;
import com.OLearning.repository.adminDashBoard.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    @Autowired
    private UserService userService; // Giả sử bạn có UserService để lấy thông tin user

    public List<Payment> findByUserName(String username) {
        User user = username != null ? userService.getUserByUsername(username) : null;
        return user != null ? paymentRepository.findByUser(user) : paymentRepository.findAll();
    }

    // Tìm kiếm theo Status
    public List<Payment> findByStatus(String status) {
        return status != null ? paymentRepository.findByStatus(status) : paymentRepository.findAll();
    }

    // Sắp xếp theo ngày (tăng dần)
    public List<Payment> findAllByDateAsc() {
        return paymentRepository.findAllByOrderByPaymentDateAsc();
    }

    // Sắp xếp theo ngày (giảm dần)
    public List<Payment> findAllByDateDesc() {
        return paymentRepository.findAllByOrderByPaymentDateDesc();
    }

    // Sắp xếp theo giá (tăng dần)
    public List<Payment> findAllByAmountAsc() {
        return paymentRepository.findAllByOrderByAmountAsc();
    }

    // Sắp xếp theo giá (giảm dần)
    public List<Payment> findAllByAmountDesc() {
        return paymentRepository.findAllByOrderByAmountDesc();
    }
}
