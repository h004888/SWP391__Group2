package com.OLearning.repository;

import com.OLearning.entity.Order;
import com.OLearning.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderOrderId(Long orderId);
    List<OrderDetail> findByOrder(Order order);
    @Query("SELECT SUM(o.unitPrice * (1 - COALESCE(c.discount, 0) / 100.0)) " +
            "FROM OrderDetail o JOIN o.course c " +
            "WHERE c.instructor.userId = :instructorId")
    Double sumRevenue(@Param("instructorId") Long instructorId);
}
