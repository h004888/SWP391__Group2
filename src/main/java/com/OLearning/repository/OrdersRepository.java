package com.OLearning.repository;

import com.OLearning.entity.Order;
import com.OLearning.entity.User;
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

    List<Order> findAll();

    Page<Order> findAll(Pageable pageable);

    List<Order> findByUserUsernameContaining(String username);

    List<Order> findAllByOrderByAmountAsc();

    List<Order> findAllByOrderByAmountDesc();

    List<Order> findAllByOrderByOrderDateAsc();


    List<Order> findAllByOrderByOrderDateDesc();

    List<Order> findByUserAndStatus(User user, String status);
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

    // Find orders by username with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByUserUsernameContaining(String username, Pageable pageable);

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