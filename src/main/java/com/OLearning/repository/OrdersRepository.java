package com.OLearning.repository;

import com.OLearning.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Order,Integer> {
//    @Query("SELECT DISTINCT o FROM Orders o JOIN FETCH o.user u JOIN FETCH o.orderDetails od JOIN FETCH od.course c")
//    List<Orders> findAllOrders();

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.user u JOIN FETCH o.orderDetails od JOIN FETCH od.course c " +
            "WHERE u.username LIKE %:username%")
    List<Order> findByUsername(@Param("username") String username);

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.user u JOIN FETCH o.orderDetails od JOIN FETCH od.course c " +
            "WHERE c.title LIKE %:courseName%")
    List<Order> findByCourseName(@Param("courseName") String courseName);

//    @Query("SELECT DISTINCT o FROM Orders o JOIN FETCH o.user u JOIN FETCH o.orderDetails od JOIN FETCH od.course c " +
//            "ORDER BY o.amount ASC")
List<Order> findByOrderByAmountAsc();

//    @Query("SELECT DISTINCT o FROM Orders o JOIN FETCH o.user u JOIN FETCH o.orderDetails od JOIN FETCH od.course c " +
//            "ORDER BY o.amount DESC")
    List<Order> findByOrderByAmountDesc();

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.user u JOIN FETCH o.orderDetails od JOIN FETCH od.course c " +
            "ORDER BY o.orderDate ASC")
    List<Order> findAllOrderByOrderDateAsc();

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.user u JOIN FETCH o.orderDetails od JOIN FETCH od.course c " +
            "ORDER BY o.orderDate DESC")
    List<Order> findAllOrderByOrderDateDesc();
}
