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
import java.util.Optional;


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

    List<Order> findByUserAndStatus(User user, String status);
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

    // Find orders by status with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByStatus(String status, Pageable pageable);

    // Find orders by username and status with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByUserUsernameContainingAndStatus(String username, String status, Pageable pageable);

    // Find orders by order type and status with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByOrderTypeAndStatus(String orderType, String status, Pageable pageable);

    // Find orders by username, order type and status with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByUserUsernameContainingAndOrderTypeAndStatus(String username, String orderType, String status, Pageable pageable);

    // Find orders by status and date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByStatusAndOrderDateBetween(
        String status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );

    // Find orders by username, status and date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByUserUsernameContainingAndStatusAndOrderDateBetween(
        String username,
        String status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );

    // Find orders by order type, status and date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByOrderTypeAndStatusAndOrderDateBetween(
        String orderType,
        String status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );

    // Find orders by username, order type, status and date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    Page<Order> findByUserUsernameContainingAndOrderTypeAndStatusAndOrderDateBetween(
        String username,
        String orderType,
        String status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );

    // Find orders by instructor ID (through course relationship) with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId")
    Page<Order> findByInstructorId(@Param("instructorId") Long instructorId, Pageable pageable);

    // Find orders by instructor ID and status with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId AND o.status = :status")
    Page<Order> findByInstructorIdAndStatus(@Param("instructorId") Long instructorId, @Param("status") String status, Pageable pageable);

    // Find orders by instructor ID and username with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId AND o.user.username LIKE %:username%")
    Page<Order> findByInstructorIdAndUserUsernameContaining(@Param("instructorId") Long instructorId, @Param("username") String username, Pageable pageable);

    // Find orders by instructor ID and order type with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId AND o.orderType = :orderType")
    Page<Order> findByInstructorIdAndOrderType(@Param("instructorId") Long instructorId, @Param("orderType") String orderType, Pageable pageable);

    // Find orders by instructor ID, username and status with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId AND o.user.username LIKE %:username% AND o.status = :status")
    Page<Order> findByInstructorIdAndUserUsernameContainingAndStatus(@Param("instructorId") Long instructorId, @Param("username") String username, @Param("status") String status, Pageable pageable);

    // Find orders by instructor ID, order type and status with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId AND o.orderType = :orderType AND o.status = :status")
    Page<Order> findByInstructorIdAndOrderTypeAndStatus(@Param("instructorId") Long instructorId, @Param("orderType") String orderType, @Param("status") String status, Pageable pageable);

    // Find orders by instructor ID, username and order type with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId AND o.user.username LIKE %:username% AND o.orderType = :orderType")
    Page<Order> findByInstructorIdAndUserUsernameContainingAndOrderType(@Param("instructorId") Long instructorId, @Param("username") String username, @Param("orderType") String orderType, Pageable pageable);

    // Find orders by instructor ID, username, order type and status with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId AND o.user.username LIKE %:username% AND o.orderType = :orderType AND o.status = :status")
    Page<Order> findByInstructorIdAndUserUsernameContainingAndOrderTypeAndStatus(@Param("instructorId") Long instructorId, @Param("username") String username, @Param("orderType") String orderType, @Param("status") String status, Pageable pageable);

    // Find orders by instructor ID and date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId AND o.orderDate BETWEEN :startDate AND :endDate")
    Page<Order> findByInstructorIdAndOrderDateBetween(@Param("instructorId") Long instructorId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // Find orders by instructor ID, status and date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId AND o.status = :status AND o.orderDate BETWEEN :startDate AND :endDate")
    Page<Order> findByInstructorIdAndStatusAndOrderDateBetween(@Param("instructorId") Long instructorId, @Param("status") String status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // Find orders by instructor ID, username and date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId AND o.user.username LIKE %:username% AND o.orderDate BETWEEN :startDate AND :endDate")
    Page<Order> findByInstructorIdAndUserUsernameContainingAndOrderDateBetween(@Param("instructorId") Long instructorId, @Param("username") String username, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // Find orders by instructor ID, order type and date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId AND o.orderType = :orderType AND o.orderDate BETWEEN :startDate AND :endDate")
    Page<Order> findByInstructorIdAndOrderTypeAndOrderDateBetween(@Param("instructorId") Long instructorId, @Param("orderType") String orderType, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // Find orders by instructor ID, username, status and date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId AND o.user.username LIKE %:username% AND o.status = :status AND o.orderDate BETWEEN :startDate AND :endDate")
    Page<Order> findByInstructorIdAndUserUsernameContainingAndStatusAndOrderDateBetween(@Param("instructorId") Long instructorId, @Param("username") String username, @Param("status") String status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // Find orders by instructor ID, order type, status and date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId AND o.orderType = :orderType AND o.status = :status AND o.orderDate BETWEEN :startDate AND :endDate")
    Page<Order> findByInstructorIdAndOrderTypeAndStatusAndOrderDateBetween(@Param("instructorId") Long instructorId, @Param("orderType") String orderType, @Param("status") String status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // Find orders by instructor ID, username, order type and date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId AND o.user.username LIKE %:username% AND o.orderType = :orderType AND o.orderDate BETWEEN :startDate AND :endDate")
    Page<Order> findByInstructorIdAndUserUsernameContainingAndOrderTypeAndOrderDateBetween(@Param("instructorId") Long instructorId, @Param("username") String username, @Param("orderType") String orderType, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // Find orders by instructor ID, username, order type, status and date range with pagination
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId AND o.user.username LIKE %:username% AND o.orderType = :orderType AND o.status = :status AND o.orderDate BETWEEN :startDate AND :endDate")
    Page<Order> findByInstructorIdAndUserUsernameContainingAndOrderTypeAndStatusAndOrderDateBetween(@Param("instructorId") Long instructorId, @Param("username") String username, @Param("orderType") String orderType, @Param("status") String status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // Find orders by instructor ID with amount sorting (ascending)
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId ORDER BY o.amount ASC")
    Page<Order> findByInstructorIdOrderByAmountAsc(@Param("instructorId") Long instructorId, Pageable pageable);

    // Find orders by instructor ID with amount sorting (descending)
    @EntityGraph(attributePaths = {"user", "user.role", "orderDetails", "orderDetails.course"})
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od WHERE od.course.instructor.userId = :instructorId ORDER BY o.amount DESC")
    Page<Order> findByInstructorIdOrderByAmountDesc(@Param("instructorId") Long instructorId, Pageable pageable);
}