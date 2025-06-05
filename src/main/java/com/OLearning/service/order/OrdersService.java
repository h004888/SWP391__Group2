package com.OLearning.service.order;

import com.OLearning.dto.order.OrdersDTO;
import com.OLearning.entity.Order;

import com.OLearning.mapper.order.OrdersMapper;
import com.OLearning.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrdersService {
    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrdersMapper ordersMapper;

    public List<OrdersDTO> getAllOrders() {
        List<Order> orders = ordersRepository.findAll();
        return ordersMapper.toDTOList(orders);
    }

    public List<OrdersDTO> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return getAllOrders();
        }
        List<Order> orders = ordersRepository.findByUsername(username);
        return ordersMapper.toDTOList(orders);
    }

    public List<OrdersDTO> findByCourseName(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            return getAllOrders();
        }
        List<Order> orders = ordersRepository.findByCourseName(courseName);
        return ordersMapper.toDTOList(orders);
    }

    public List<OrdersDTO> sortByAmount(String direction) {
        List<Order> orders ;
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
        List<Order> orders;
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
