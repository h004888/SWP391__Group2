package com.OLearning.service.adminDashBoard.impl;

import com.OLearning.dto.adminDashBoard.OrdersDTO;
import com.OLearning.entity.Orders;

import com.OLearning.mapper.adminDashBoard.OrdersMapper;
import com.OLearning.service.adminDashBoard.OrdersService;
import com.OLearning.repository.adminDashBoard.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrdersServiceImpl implements OrdersService{
    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    public List<OrdersDTO> getAllOrders() {
        List<Orders> orders = ordersRepository.findAll();
        return ordersMapper.toDTOList(orders);
    }

    @Override
    public List<OrdersDTO> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return getAllOrders();
        }
        List<Orders> orders = ordersRepository.findByUserUsernameContaining(username);
        return ordersMapper.toDTOList(orders);
    }

    @Override
    public List<OrdersDTO> findByCourseName(String courseName) {
        if (courseName == null || courseName.trim().isEmpty()) {
            return getAllOrders();
        }
        List<Orders> orders = ordersRepository.findByOrderDetailsCourseTitleContaining(courseName);
        return ordersMapper.toDTOList(orders);
    }

    @Override
    public List<OrdersDTO> sortByAmount(String direction) {
        List<Orders> orders ;
        if ("asc".equalsIgnoreCase(direction)) {
            orders = ordersRepository.findAllByOrderByAmountAsc();
        } else if ("desc".equalsIgnoreCase(direction)) {
            orders = ordersRepository.findAllByOrderByAmountDesc();
        } else {
            orders = ordersRepository.findAll(); // All
        }
        return ordersMapper.toDTOList(orders);
    }

    @Override
    public List<OrdersDTO> sortByDate(String direction) {
        List<Orders> orders;
        if ("asc".equalsIgnoreCase(direction)) {
            orders = ordersRepository.findAllByOrderByOrderDateAsc();
        } else if ("desc".equalsIgnoreCase(direction)) {
            orders = ordersRepository.findAllByOrderByOrderDateDesc();
        } else {
            orders = ordersRepository.findAll(); // All
        }
        return ordersMapper.toDTOList(orders);
    }
}
