package com.OLearning.service.order;

import com.OLearning.dto.order.OrdersDTO;
import com.OLearning.dto.order.InstructorOrderDTO;
import com.OLearning.entity.Order;
import com.OLearning.entity.OrderDetail;
import com.OLearning.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Service
public interface OrdersService {
    List<OrdersDTO> getAllOrders();

    List<OrdersDTO> findByUsername(String username);

    List<OrdersDTO> sortByAmount(String direction);

    List<OrdersDTO> sortByDate(String direction);

    List<OrderDetail> getOrderDetailsByOrderId(Long orderId);

    Page<OrdersDTO> filterAndSortOrders(String username, String amountDirection, String orderType, String startDate, String endDate, int page, int size);

    Page<OrdersDTO> filterAndSortOrdersWithStatus(String username, String amountDirection, String orderType, String startDate, String endDate, String status, int page, int size);

    Page<OrdersDTO> filterAndSortOrdersWithStatusOfInstructor(String username, String amountDirection, String orderType, String startDate, String endDate, String status, int page, int size, Long instructorId);

    // New methods for instructor-specific orders
    Page<InstructorOrderDTO> filterAndSortInstructorOrders(String username, String amountDirection, String orderType, String startDate, String endDate, String status, int page, int size, Long instructorId);

    List<OrderDetail> getInstructorOrderDetailsByOrderId(Long orderId, Long instructorId);

    Map<String, Double> getRevenuePerMonth();

    Map<String, Double> getRevenueByDateRange(LocalDate startDate, LocalDate endDate);

    void markOrderAsPaid(Long orderId, String refCode);

    String getOrderStatusById(Long orderId);

    Order createOrder(User user, double amount, String orderType, String description);

    Order getOrderById(Long orderId);

    void saveOrder(Order order);

    void saveOrderDetail(OrderDetail orderDetail);
}
