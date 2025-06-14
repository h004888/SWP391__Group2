package com.OLearning.repository;

import com.OLearning.entity.OrderDetail;
import com.OLearning.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderOrderId(Long orderId);
    List<OrderDetail> findByOrder(Orders order);
}
