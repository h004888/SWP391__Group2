package com.OLearning.service.adminDashBoard;

import com.OLearning.dto.adminDashBoard.OrdersDTO;
import com.OLearning.entity.Orders;

import com.OLearning.mapper.adminDashBoard.OrdersMapper;
import com.OLearning.repository.adminDashBoard.OrdersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class OrdersService {
    @Autowired
    private OrdersRepo ordersRepository;

    @Autowired
    private OrdersMapper ordersMapper;

    public List<OrdersDTO> getAllOrders() {
        List<Orders> orders = ordersRepository.findAll();
        return ordersMapper.toDTOList(orders);
    }

    public List<OrdersDTO> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return getAllOrders();
        }
        List<Orders> orders = ordersRepository.findByUsername(username);
        return ordersMapper.toDTOList(orders);
    }

    public List<OrdersDTO> findByCourseName(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            return getAllOrders();
        }
        List<Orders> orders = ordersRepository.findByCourseName(courseName);
        return ordersMapper.toDTOList(orders);
    }

    public List<OrdersDTO> sortByAmount(String direction) {
        List<Orders> orders ;
        if ("asc".equalsIgnoreCase(direction)) {
            orders = ordersRepository.findByOrderByAmountAsc();
        } else if ("desc".equalsIgnoreCase(direction)) {
            orders = ordersRepository.findByOrderByAmountDesc();
        } else {
            orders = ordersRepository.findAll(); // All
        }
        return ordersMapper.toDTOList(orders);
    }

    public List<OrdersDTO> sortByDate(String direction) {
        List<Orders> orders;
        if ("asc".equalsIgnoreCase(direction)) {
            orders = ordersRepository.findAllOrderByOrderDateAsc();
        } else if ("desc".equalsIgnoreCase(direction)) {
            orders = ordersRepository.findAllOrderByOrderDateDesc();
        } else {
            orders = ordersRepository.findAll(); // All
        }
        return ordersMapper.toDTOList(orders);
    }
}
