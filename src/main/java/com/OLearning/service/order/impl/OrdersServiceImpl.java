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
import com.OLearning.entity.User;
import java.util.Optional;
import com.OLearning.service.cart.CartService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrdersServiceImpl implements OrdersService {

    private static final Logger logger = LoggerFactory.getLogger(OrdersServiceImpl.class);

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private CartService cartService;

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
        return orderDetailRepository.findByOrderOrderId(orderId);
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
    public Page<OrdersDTO> filterAndSortOrdersWithStatus(String username, String amountDirection, String orderType, String startDate, String endDate, String status, int page, int size) {
        Pageable pageable = createPageable(amountDirection, page, size);
        
        if (hasDateRange(startDate, endDate)) {
            return filterWithDateRange(username, orderType, status, startDate, endDate, pageable);
        } else {
            return filterWithoutDateRange(username, orderType, status, pageable);
        }
    }

    private Pageable createPageable(String amountDirection, int page, int size) {
        if (amountDirection != null && !amountDirection.trim().isEmpty()) {
            Sort sort = "asc".equalsIgnoreCase(amountDirection) ? 
                Sort.by("amount").ascending() : Sort.by("amount").descending();
            return PageRequest.of(page, size, sort);
        } else {
            return PageRequest.of(page, size, Sort.by("orderDate").descending());
        }
    }

    private boolean hasDateRange(String startDate, String endDate) {
        return startDate != null && !startDate.trim().isEmpty() && 
               endDate != null && !endDate.trim().isEmpty();
    }

    private Page<OrdersDTO> filterWithDateRange(String username, String orderType, String status, String startDate, String endDate, Pageable pageable) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            LocalDateTime endDateTime = end.plusDays(1).atStartOfDay().minusSeconds(1);
            LocalDateTime startDateTime = start.atStartOfDay();
            
            Page<Order> ordersPage = getOrdersWithDateRange(username, orderType, status, startDateTime, endDateTime, pageable);
            return ordersPage.map(ordersMapper::toDTO);
        } catch (Exception e) {
            // If date parsing fails, fall back to filtering without date range
            return filterWithoutDateRange(username, orderType, status, pageable);
        }
    }

    private Page<Order> getOrdersWithDateRange(String username, String orderType, String status, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable) {
        FilterCriteria criteria = new FilterCriteria(username, orderType, status);
        
        switch (criteria.getFilterType()) {
            case ALL_FILTERS:
                return ordersRepository.findByUserUsernameContainingAndOrderTypeAndStatusAndOrderDateBetween(
                    username, orderType, status, startDateTime, endDateTime, pageable);
            case USERNAME_AND_STATUS:
                return ordersRepository.findByUserUsernameContainingAndStatusAndOrderDateBetween(
                    username, status, startDateTime, endDateTime, pageable);
            case ORDER_TYPE_AND_STATUS:
                return ordersRepository.findByOrderTypeAndStatusAndOrderDateBetween(
                    orderType, status, startDateTime, endDateTime, pageable);
            case STATUS_ONLY:
                return ordersRepository.findByStatusAndOrderDateBetween(
                    status, startDateTime, endDateTime, pageable);
            case USERNAME_AND_ORDER_TYPE:
                return ordersRepository.findByUserUsernameContainingAndOrderTypeAndOrderDateBetween(
                    username, orderType, startDateTime, endDateTime, pageable);
            case USERNAME_ONLY:
                return ordersRepository.findByUserUsernameContainingAndOrderDateBetween(
                    username, startDateTime, endDateTime, pageable);
            case ORDER_TYPE_ONLY:
                return ordersRepository.findByOrderTypeAndOrderDateBetween(
                    orderType, startDateTime, endDateTime, pageable);
            default:
                return ordersRepository.findByOrderDateBetween(startDateTime, endDateTime, pageable);
        }
    }

    private Page<OrdersDTO> filterWithoutDateRange(String username, String orderType, String status, Pageable pageable) {
        Page<Order> ordersPage = getOrdersWithoutDateRange(username, orderType, status, pageable);
        return ordersPage.map(ordersMapper::toDTO);
    }

    private Page<Order> getOrdersWithoutDateRange(String username, String orderType, String status, Pageable pageable) {
        FilterCriteria criteria = new FilterCriteria(username, orderType, status);
        
        switch (criteria.getFilterType()) {
            case ALL_FILTERS:
                return ordersRepository.findByUserUsernameContainingAndOrderTypeAndStatus(username, orderType, status, pageable);
            case USERNAME_AND_STATUS:
                return ordersRepository.findByUserUsernameContainingAndStatus(username, status, pageable);
            case ORDER_TYPE_AND_STATUS:
                return ordersRepository.findByOrderTypeAndStatus(orderType, status, pageable);
            case STATUS_ONLY:
                return ordersRepository.findByStatus(status, pageable);
            case USERNAME_AND_ORDER_TYPE:
                return ordersRepository.findByUserUsernameContainingAndOrderType(username, orderType, pageable);
            case USERNAME_ONLY:
                return ordersRepository.findByUserUsernameContaining(username, pageable);
            case ORDER_TYPE_ONLY:
                return ordersRepository.findByOrderType(orderType, pageable);
            default:
                return ordersRepository.findAll(pageable);
        }
    }

    // Helper class to determine filter type
    private static class FilterCriteria {
        private final String username;
        private final String orderType;
        private final String status;

        public FilterCriteria(String username, String orderType, String status) {
            this.username = username;
            this.orderType = orderType;
            this.status = status;
        }

        public FilterType getFilterType() {
            boolean hasUsername = username != null && !username.trim().isEmpty();
            boolean hasOrderType = orderType != null && !orderType.trim().isEmpty();
            boolean hasStatus = status != null && !status.trim().isEmpty();

            if (hasUsername && hasOrderType && hasStatus) {
                return FilterType.ALL_FILTERS;
            } else if (hasUsername && hasStatus) {
                return FilterType.USERNAME_AND_STATUS;
            } else if (hasOrderType && hasStatus) {
                return FilterType.ORDER_TYPE_AND_STATUS;
            } else if (hasStatus) {
                return FilterType.STATUS_ONLY;
            } else if (hasUsername && hasOrderType) {
                return FilterType.USERNAME_AND_ORDER_TYPE;
            } else if (hasUsername) {
                return FilterType.USERNAME_ONLY;
            } else if (hasOrderType) {
                return FilterType.ORDER_TYPE_ONLY;
            } else {
                return FilterType.NO_FILTERS;
            }
        }
    }

    private enum FilterType {
        ALL_FILTERS,
        USERNAME_AND_STATUS,
        ORDER_TYPE_AND_STATUS,
        STATUS_ONLY,
        USERNAME_AND_ORDER_TYPE,
        USERNAME_ONLY,
        ORDER_TYPE_ONLY,
        NO_FILTERS
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

    @Override
    public void markOrderAsPaid(Long orderId) {
        Optional<Order> optionalOrder = ordersRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus("completed");
            ordersRepository.save(order);
        }
    }

    @Override
    public String getOrderStatusById(Long orderId) {
        Optional<Order> optionalOrder = ordersRepository.findById(orderId);
        return optionalOrder.map(Order::getStatus).orElse("NOT_FOUND");
    }

    @Override
    public Order createOrder(User user, double amount, String orderType, String description) {
        Order order = new Order();
        order.setUser(user);
        order.setAmount(amount);
        order.setOrderType(orderType);
        order.setDescription(description);
        order.setStatus("pending");
        order.setOrderDate(java.time.LocalDateTime.now());
        Order savedOrder = ordersRepository.save(order);
        return savedOrder;
    }

    @Override
    public Order getOrderById(Long orderId) {
        return ordersRepository.findById(orderId).orElse(null);
    }

    @Override
    public void saveOrder(Order order) {
        ordersRepository.save(order);
    }
}
