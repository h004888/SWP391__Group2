package com.OLearning.service.adminDashBoard;

import com.OLearning.dto.adminDashBoard.OrdersDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrdersService {
    List<OrdersDTO> getAllOrders();
    List<OrdersDTO> findByUsername(String username);
    List<OrdersDTO> findByCourseName(String courseName);
    List<OrdersDTO> sortByAmount(String direction);
    List<OrdersDTO> sortByDate(String direction);
}
