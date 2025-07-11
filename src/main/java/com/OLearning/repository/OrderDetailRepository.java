package com.OLearning.repository;

import com.OLearning.entity.Order;
import com.OLearning.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderOrderId(Long orderId);
    List<OrderDetail> findByOrder(Order order);
}
