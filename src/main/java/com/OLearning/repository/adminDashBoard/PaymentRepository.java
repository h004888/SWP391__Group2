package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.Payment;
import com.OLearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    // Tìm kiếm theo User
    List<Payment> findByUser(User user);

    // Tìm kiếm theo Status
    List<Payment> findByStatus(String status);

    // Sắp xếp theo PaymentDate (ngày)
    List<Payment> findAllByOrderByPaymentDateAsc();
    List<Payment> findAllByOrderByPaymentDateDesc();

    // Sắp xếp theo Amount (giá)
    List<Payment> findAllByOrderByAmountAsc();
    List<Payment> findAllByOrderByAmountDesc();
}
