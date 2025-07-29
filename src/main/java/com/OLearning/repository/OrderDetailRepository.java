package com.OLearning.repository;

import com.OLearning.entity.Order;
import com.OLearning.entity.OrderDetail;
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


    // Doanh thu theo ngày (7 ngày gần nhất)
    @Query(value = """
        SELECT YEAR(o.OrderDate), MONTH(o.OrderDate), DAY(o.OrderDate),
               SUM(o.Amount), COUNT(DISTINCT o.OrderID)
        FROM Orders o
        JOIN OrderDetail od ON o.OrderID = od.OrderID
        JOIN Courses c ON od.CourseID = c.CourseID
        WHERE c.InstructorID = :instructorId and o.status = 'paid'
          AND o.OrderDate >= :start AND o.OrderDate <= :end
        GROUP BY YEAR(o.OrderDate), MONTH(o.OrderDate), DAY(o.OrderDate)
        ORDER BY YEAR(o.OrderDate), MONTH(o.OrderDate), DAY(o.OrderDate)
    """, nativeQuery = true)
    List<Object[]> findRevenueAndOrdersByDay(@Param("instructorId") Long instructorId,
                                             @Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    // Doanh thu theo tháng (12 tháng gần nhất)
    @Query(value = """
        SELECT YEAR(o.OrderDate), MONTH(o.OrderDate),
               SUM(o.Amount), COUNT(DISTINCT o.OrderID)
        FROM Orders o
        JOIN OrderDetail od ON o.OrderID = od.OrderID
        JOIN Courses c ON od.CourseID = c.CourseID
        WHERE c.InstructorID = :instructorId and o.status = 'paid'
          AND o.OrderDate >= :start AND o.OrderDate <= :end
        GROUP BY YEAR(o.OrderDate), MONTH(o.OrderDate)
        ORDER BY YEAR(o.OrderDate), MONTH(o.OrderDate)
    """, nativeQuery = true)
    List<Object[]> findRevenueAndOrdersByMonth(@Param("instructorId") Long instructorId,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    // Doanh thu theo quý (4 quý gần nhất)
    @Query(value = """
        SELECT YEAR(o.OrderDate), DATEPART(quarter, o.OrderDate),
               SUM(o.Amount), COUNT(DISTINCT o.OrderID)
        FROM Orders o
        JOIN OrderDetail od ON o.OrderID = od.OrderID
        JOIN Courses c ON od.CourseID = c.CourseID
        WHERE c.InstructorID = :instructorId and o.status = 'paid'
          AND o.OrderDate >= :start AND o.OrderDate <= :end
        GROUP BY YEAR(o.OrderDate), DATEPART(quarter, o.OrderDate)
        ORDER BY YEAR(o.OrderDate), DATEPART(quarter, o.OrderDate)
    """, nativeQuery = true)
    List<Object[]> findRevenueAndOrdersByQuarter(@Param("instructorId") Long instructorId,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);


}
