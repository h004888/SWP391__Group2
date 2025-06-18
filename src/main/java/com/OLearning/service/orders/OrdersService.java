package com.OLearning.service.orders;

import com.OLearning.dto.orders.OrdersDTO;
import com.OLearning.entity.OrderDetail;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrdersService {
    List<OrdersDTO> getAllOrders();
    List<OrdersDTO> findByUsername(String username);
    List<OrderDetail> getOrderDetailsByOrderId(Long orderId);
    Page<OrdersDTO> filterAndSortOrders(String username, String amountDirection, String dateDirection, int page, int size);
}
