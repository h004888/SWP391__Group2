package com.OLearning.service.order;

import com.OLearning.dto.order.OrdersDTO;
import com.OLearning.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrdersService {
    List<OrdersDTO> getAllOrders();
    List<OrdersDTO> findByUsername(String username);
    List<OrdersDTO> sortByAmount(String direction);
    List<OrdersDTO> sortByDate(String direction);
    List<OrderDetail> getOrderDetailsByOrderId(Long orderId);
    Page<OrdersDTO> filterAndSortOrders(String username, String amountDirection, String dateDirection, int page, int size);
}
