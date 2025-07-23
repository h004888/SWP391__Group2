package com.OLearning.repository;

import com.OLearning.entity.Order;
import com.OLearning.entity.OrderDetail;
import com.OLearning.dto.RevenueGroupDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderOrderId(Long orderId);
    List<OrderDetail> findByOrder(Order order);
    @Query("SELECT SUM(o.unitPrice * (1 - COALESCE(c.discount, 0) / 100.0)) " +
            "FROM OrderDetail o JOIN o.course c " +
            "WHERE c.instructor.userId = :instructorId")
    Double sumRevenue(@Param("instructorId") Long instructorId);

    // Lấy doanh thu và số lượng bán từng khóa học của instructor
    @Query("SELECT c.title, COUNT(o), SUM(o.unitPrice * (1 - COALESCE(c.discount, 0) / 100.0)) " +
            "FROM OrderDetail o JOIN o.course c " +
            "WHERE c.instructor.userId = :instructorId " +
            "GROUP BY c.courseId, c.title")
    List<Object[]> findCourseSalesAndRevenueByInstructor(@Param("instructorId") Long instructorId);

    // Doanh thu theo tháng (trả về: year, month, revenue)
    @Query("SELECT YEAR(od.order.orderDate), MONTH(od.order.orderDate), SUM(od.unitPrice * (1 - COALESCE(c.discount, 0) / 100.0)) " +
           "FROM OrderDetail od JOIN od.course c " +
           "WHERE c.instructor.userId = :instructorId AND od.order.orderDate >= :start AND od.order.orderDate <= :end " +
           "GROUP BY YEAR(od.order.orderDate), MONTH(od.order.orderDate) " +
           "ORDER BY YEAR(od.order.orderDate), MONTH(od.order.orderDate)")
    List<Object[]> findRevenueByMonth(@Param("instructorId") Long instructorId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    // Doanh thu theo năm (trả về: year, revenue)
    @Query("SELECT YEAR(od.order.orderDate), SUM(od.unitPrice * (1 - COALESCE(c.discount, 0) / 100.0)) " +
           "FROM OrderDetail od JOIN od.course c " +
           "WHERE c.instructor.userId = :instructorId AND od.order.orderDate >= :start AND od.order.orderDate <= :end " +
           "GROUP BY YEAR(od.order.orderDate) " +
           "ORDER BY YEAR(od.order.orderDate)")
    List<Object[]> findRevenueByYear(@Param("instructorId") Long instructorId,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

    // Doanh thu theo ngày (trả về: year, month, day, revenue)
    @Query("SELECT YEAR(od.order.orderDate), MONTH(od.order.orderDate), DAY(od.order.orderDate), SUM(od.unitPrice * (1 - COALESCE(c.discount, 0) / 100.0)) " +
           "FROM OrderDetail od JOIN od.course c " +
           "WHERE c.instructor.userId = :instructorId AND od.order.orderDate >= :start AND od.order.orderDate <= :end " +
           "GROUP BY YEAR(od.order.orderDate), MONTH(od.order.orderDate), DAY(od.order.orderDate) " +
           "ORDER BY YEAR(od.order.orderDate), MONTH(od.order.orderDate), DAY(od.order.orderDate)")
    List<Object[]> findRevenueByDay(@Param("instructorId") Long instructorId,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);
}
