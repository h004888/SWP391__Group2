package com.OLearning.repository;

import com.OLearning.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long> {

    // Tìm tất cả đơn hàng có load user, orderDetails và course
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    List<Order> findAll();

    // Tìm tất cả đơn hàng với phân trang
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findAll(Pageable pageable);

    // Tìm đơn hàng theo username (LIKE)
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    List<Order> findByUserUsernameContaining(String username);

    // Tìm đơn hàng theo username với phân trang
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByUserUsernameContaining(String username, Pageable pageable);

    // Sắp xếp theo amount tăng dần
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    List<Order> findAllByOrderByAmountAsc();

    // Sắp xếp theo amount giảm dần
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    List<Order> findAllByOrderByAmountDesc();

    // Sắp xếp theo ngày tăng dần
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    List<Order> findAllByOrderByOrderDateAsc();

    // Sắp xếp theo ngày giảm dần
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    List<Order> findAllByOrderByOrderDateDesc();

    // Combined username filtering and sorting with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByUserUsernameContainingOrderByAmountAsc(String username, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByUserUsernameContainingOrderByAmountDesc(String username, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByUserUsernameContainingOrderByOrderDateAsc(String username, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByUserUsernameContainingOrderByOrderDateDesc(String username, Pageable pageable);

    @Query("SELECT MONTH(o.orderDate) as month, SUM(o.amount) as totalAmount " +
            "FROM Order o " +
            "WHERE YEAR(o.orderDate) = :year " +
            "GROUP BY MONTH(o.orderDate) " +
            "ORDER BY MONTH(o.orderDate)")
    List<Object[]> getMonthlyRevenue(@Param("year") int year);

    @Query(value = "SELECT FORMAT(o.orderDate, 'yyyy-MM') as monthYear, SUM(o.amount) as totalAmount " +
            "FROM Orders o " +
            "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY FORMAT(o.orderDate, 'yyyy-MM') " +
            "ORDER BY FORMAT(o.orderDate, 'yyyy-MM')", nativeQuery = true)
    List<Object[]> getRevenueByDateRange(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    // Find orders by username and date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByUserUsernameContainingAndOrderDateBetween(
        String username, 
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );

    // Find orders by date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByOrderDateBetween(
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );

    // Find orders by username and order type with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByUserUsernameContainingAndOrderType(String username, String orderType, Pageable pageable);

    // Find orders by order type with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByOrderType(String orderType, Pageable pageable);

    // Find orders by username, order type and date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByUserUsernameContainingAndOrderTypeAndOrderDateBetween(
        String username, 
        String orderType,
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );

    // Find orders by order type and date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByOrderTypeAndOrderDateBetween(
        String orderType,
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );

}