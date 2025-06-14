package com.OLearning.service.orders.Impl;

import com.OLearning.dto.cart.CartDTO;
import com.OLearning.dto.cart.CartDetailDTO;
import com.OLearning.dto.orders.OrdersDTO;
import com.OLearning.entity.*;
import com.OLearning.mapper.orders.OrdersMapper;
import com.OLearning.repository.*;
import com.OLearning.service.cart.CartService;
import com.OLearning.service.orders.OrdersService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

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
    public List<OrdersDTO> sortByAmount(String direction) {
        List<Orders> orders;
        if ("asc".equalsIgnoreCase(direction)) {
            orders = ordersRepository.findAllByOrderByAmountAsc();
        } else if ("desc".equalsIgnoreCase(direction)) {
            orders = ordersRepository.findAllByOrderByAmountDesc();
        } else {
            orders = ordersRepository.findAll();
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
            orders = ordersRepository.findAll();
        }
        return ordersMapper.toDTOList(orders);
    }

    @Override
    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderOrderId(orderId);
    }

    @Override
    public Page<OrdersDTO> filterAndSortOrders(String username, String amountDirection, String dateDirection, int page, int size) {
        Pageable pageable;
        if (dateDirection != null && !dateDirection.trim().isEmpty()) {
            pageable = PageRequest.of(page, size, "asc".equalsIgnoreCase(dateDirection) ? Sort.by("orderDate").ascending() : Sort.by("orderDate").descending());
        } else if (amountDirection != null && !amountDirection.trim().isEmpty()) {
            pageable = PageRequest.of(page, size, "asc".equalsIgnoreCase(amountDirection) ? Sort.by("amount").ascending() : Sort.by("amount").descending());
        } else {
            pageable = PageRequest.of(page, size);
        }

        Page<Orders> ordersPage;
        if (username != null && !username.trim().isEmpty()) {
            ordersPage = ordersRepository.findByUserUsernameContaining(username, pageable);
        } else {
            ordersPage = ordersRepository.findAll(pageable);
        }

        return ordersPage.map(ordersMapper::toDTO);
    }

}