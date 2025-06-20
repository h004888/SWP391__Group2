package com.OLearning.service.order.impl;

import com.OLearning.dto.order.OrdersDTO;
import com.OLearning.entity.Order;
import com.OLearning.entity.OrderDetail;
import com.OLearning.mapper.order.OrdersMapper;
import com.OLearning.repository.OrderDetailRepository;
import com.OLearning.repository.OrdersRepository;
import com.OLearning.service.order.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Order> orders = ordersRepository.findAll();
        return ordersMapper.toDTOList(orders);
    }

    @Override
    public List<OrdersDTO> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return getAllOrders();
        }
        List<Order> orders = ordersRepository.findByUserUsernameContaining(username);
        return ordersMapper.toDTOList(orders);
    }

    @Override
    public List<OrdersDTO> sortByAmount(String direction) {
        List<Order> orders;
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
        List<Order> orders;
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
        return orderDetailRepository.findByOrdersOrderId(orderId);
    }

    @Override
    public Page<OrdersDTO> filterAndSortOrders(String username, String amountDirection, String orderType, String startDate, String endDate, int page, int size) {
        Pageable pageable;
        if (startDate != null && !startDate.trim().isEmpty() && endDate != null && !endDate.trim().isEmpty()) {
            try {
                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);
                // Set end date to end of day
                LocalDateTime endDateTime = end.plusDays(1).atStartOfDay().minusSeconds(1);
                
                pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
                Page<Order> ordersPage;
                if (username != null && !username.trim().isEmpty() && orderType != null && !orderType.trim().isEmpty()) {
                    ordersPage = ordersRepository.findByUserUsernameContainingAndOrderTypeAndOrderDateBetween(
                        username, 
                        orderType,
                        start.atStartOfDay(), 
                        endDateTime, 
                        pageable
                    );
                } else if (username != null && !username.trim().isEmpty()) {
                    ordersPage = ordersRepository.findByUserUsernameContainingAndOrderDateBetween(
                        username, 
                        start.atStartOfDay(), 
                        endDateTime, 
                        pageable
                    );
                } else if (orderType != null && !orderType.trim().isEmpty()) {
                    ordersPage = ordersRepository.findByOrderTypeAndOrderDateBetween(
                        orderType,
                        start.atStartOfDay(), 
                        endDateTime, 
                        pageable
                    );
                } else {
                    ordersPage = ordersRepository.findByOrderDateBetween(
                        start.atStartOfDay(), 
                        endDateTime, 
                        pageable
                    );
                }
                return ordersPage.map(ordersMapper::toDTO);
            } catch (Exception e) {
                // If date parsing fails, fall back to default sorting
                pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
            }
        } else if (amountDirection != null && !amountDirection.trim().isEmpty()) {
            pageable = PageRequest.of(page, size, "asc".equalsIgnoreCase(amountDirection) ? 
                Sort.by("amount").ascending() : Sort.by("amount").descending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        }

        Page<Order> ordersPage;
        if (username != null && !username.trim().isEmpty() && orderType != null && !orderType.trim().isEmpty()) {
            ordersPage = ordersRepository.findByUserUsernameContainingAndOrderType(username, orderType, pageable);
        } else if (username != null && !username.trim().isEmpty()) {
            ordersPage = ordersRepository.findByUserUsernameContaining(username, pageable);
        } else if (orderType != null && !orderType.trim().isEmpty()) {
            ordersPage = ordersRepository.findByOrderType(orderType, pageable);
        } else {
            ordersPage = ordersRepository.findAll(pageable);
        }

        return ordersPage.map(ordersMapper::toDTO);
    }

    @Override
    public Map<String, Double> getRevenuePerMonth() {
        Map<String, Double> revenuePerMonth = new HashMap<>();
        // Initialize all months with 0
        LocalDate now = LocalDate.now();
        for (int i = 1; i <= 12; i++) {
            String monthKey = String.format("%d-%02d", now.getYear(), i);
            revenuePerMonth.put(monthKey, 0.0);
        }
        List<Object[]> monthlyRevenue = ordersRepository.getMonthlyRevenue(now.getYear());
        for (Object[] result : monthlyRevenue) {
            String month = result[0].toString();
            Double totalAmount = (Double) result[1];
            String monthKey = String.format("%d-%02d", now.getYear(), Integer.parseInt(month));
            revenuePerMonth.put(monthKey, totalAmount);
        }
        return revenuePerMonth;
    }

    @Override
    public Map<String, Double> getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        Map<String, Double> revenueByDate = new HashMap<>();
        List<Object[]> revenueData = ordersRepository.getRevenueByDateRange(startDate, endDate);
        for (Object[] result : revenueData) {
            String monthYear = (String) result[0];
            Double totalAmount = (Double) result[1];
            revenueByDate.put(monthYear, totalAmount);
        }
        return revenueByDate;
    }
}
