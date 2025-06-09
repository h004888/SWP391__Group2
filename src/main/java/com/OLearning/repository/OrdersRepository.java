package com.OLearning.repository;

import com.OLearning.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}