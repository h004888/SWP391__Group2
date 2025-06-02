package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.Orders;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepo extends JpaRepository<Orders,Integer> {
//    @Query("SELECT DISTINCT o FROM Orders o JOIN FETCH o.user u JOIN FETCH o.orderDetails od JOIN FETCH od.course c")
//    List<Orders> findAllOrders();

    @Query("SELECT DISTINCT o FROM Orders o JOIN FETCH o.user u JOIN FETCH o.orderDetails od JOIN FETCH od.course c " +
            "WHERE u.username LIKE %:username%")
    List<Orders> findByUsername(@Param("username") String username);

    @Query("SELECT DISTINCT o FROM Orders o JOIN FETCH o.user u JOIN FETCH o.orderDetails od JOIN FETCH od.course c " +
            "WHERE c.title LIKE %:courseName%")
    List<Orders> findByCourseName(@Param("courseName") String courseName);

//    @Query("SELECT DISTINCT o FROM Orders o JOIN FETCH o.user u JOIN FETCH o.orderDetails od JOIN FETCH od.course c " +
//            "ORDER BY o.amount ASC")
List<Orders> findByOrderByAmountAsc();

//    @Query("SELECT DISTINCT o FROM Orders o JOIN FETCH o.user u JOIN FETCH o.orderDetails od JOIN FETCH od.course c " +
//            "ORDER BY o.amount DESC")
    List<Orders> findByOrderByAmountDesc();

    @Query("SELECT DISTINCT o FROM Orders o JOIN FETCH o.user u JOIN FETCH o.orderDetails od JOIN FETCH od.course c " +
            "ORDER BY o.orderDate ASC")
    List<Orders> findAllOrderByOrderDateAsc();

    @Query("SELECT DISTINCT o FROM Orders o JOIN FETCH o.user u JOIN FETCH o.orderDetails od JOIN FETCH od.course c " +
            "ORDER BY o.orderDate DESC")
    List<Orders> findAllOrderByOrderDateDesc();
}
