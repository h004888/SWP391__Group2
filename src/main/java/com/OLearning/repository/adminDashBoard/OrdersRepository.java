package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.Orders;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders,Integer> {
    // Tìm tất cả đơn hàng có load user, orderDetails và course
    @EntityGraph(attributePaths = {"user", "orderDetails", "orderDetails.course"})
    List<Orders> findAll();

    // Tìm đơn hàng theo username (LIKE)
    @EntityGraph(attributePaths = {"user", "orderDetails", "orderDetails.course"})
    List<Orders> findByUserUsernameContaining(String username);

    // Tìm đơn hàng theo tên khóa học (LIKE)
    @EntityGraph(attributePaths = {"user", "orderDetails", "orderDetails.course"})
    List<Orders> findByOrderDetailsCourseTitleContaining(String courseName);

    // Sắp xếp theo amount tăng dần
    @EntityGraph(attributePaths = {"user", "orderDetails", "orderDetails.course"})
    List<Orders> findAllByOrderByAmountAsc();

    // Sắp xếp theo amount giảm dần
    @EntityGraph(attributePaths = {"user", "orderDetails", "orderDetails.course"})
    List<Orders> findAllByOrderByAmountDesc();

    // Sắp xếp theo ngày tăng dần
    @EntityGraph(attributePaths = {"user", "orderDetails", "orderDetails.course"})
    List<Orders> findAllByOrderByOrderDateAsc();

    // Sắp xếp theo ngày giảm dần
    @EntityGraph(attributePaths = {"user", "orderDetails", "orderDetails.course"})
    List<Orders> findAllByOrderByOrderDateDesc();
}
