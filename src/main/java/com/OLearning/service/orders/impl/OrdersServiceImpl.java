package com.OLearning.service.orders.impl;

import com.OLearning.dto.orders.OrdersDTO;
import com.OLearning.entity.*;
import com.OLearning.mapper.orders.OrdersMapper;
import com.OLearning.repository.*;
import com.OLearning.service.orders.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

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
    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderOrderId(orderId);
    }

    @Override
    public Page<OrdersDTO> filterAndSortOrders(String username, String amountDirection, String dateDirection, int page, int size) {
        // Build sort dynamically
        Sort sort = Sort.unsorted();
        if (amountDirection != null && !amountDirection.trim().isEmpty()) {
            Sort.Direction direction = "asc".equalsIgnoreCase(amountDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = sort.and(Sort.by(direction, "amount"));
        }
        if (dateDirection != null && !dateDirection.trim().isEmpty()) {
            Sort.Direction direction = "asc".equalsIgnoreCase(dateDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = sort.and(Sort.by(direction, "orderDate"));
        }

        // Create Pageable with sort
        Pageable pageable = PageRequest.of(page, size, sort);

        // Query based on username
        Page<Orders> ordersPage;
        String normalizedUsername = username != null ? username.trim() : "";
        if (!normalizedUsername.isEmpty()) {
            ordersPage = ordersRepository.findByUserUsernameContaining(normalizedUsername, pageable);
        } else {
            ordersPage = ordersRepository.findAll(pageable);
        }

        return ordersPage.map(ordersMapper::toDTO);
    }

}