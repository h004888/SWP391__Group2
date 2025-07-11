package com.OLearning.service.order.impl;

import com.OLearning.dto.order.OrdersDTO;
import com.OLearning.dto.order.InstructorOrderDTO;
import com.OLearning.entity.*;
import com.OLearning.mapper.order.OrdersMapper;
import com.OLearning.mapper.order.InstructorOrderMapper;
import com.OLearning.repository.*;
import com.OLearning.service.order.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;
import com.OLearning.service.cart.CartService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.OLearning.service.voucher.VoucherService;

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
    private InstructorOrderMapper instructorOrderMapper;

    @Autowired
    private CartService cartService;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CoinTransactionRepository coinTransactionRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private UserVoucherRepository userVoucherRepository;

    @Autowired
    private VoucherRepository voucherRepository;

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

    @Override
    public Page<OrdersDTO> filterAndSortOrdersWithStatusOfInstructor(String username, String amountDirection, String orderType, String startDate, String endDate, String status, int page, int size, Long instructorId) {
        Pageable pageable = createInstructorPageable(amountDirection, page, size, instructorId);

        if (hasDateRange(startDate, endDate)) {
            return filterWithDateRangeInstructor(username, orderType, status, startDate, endDate, pageable, instructorId);
        } else {
            return filterWithoutDateRangeInstructor(username, orderType, status, pageable, instructorId);
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

    private Pageable createInstructorPageable(String amountDirection, int page, int size, Long instructorId) {
        if (amountDirection != null && !amountDirection.trim().isEmpty()) {
            // For instructor, we need to sort by the calculated instructor amount
            // Since we can't sort by calculated field in JPA, we'll sort by order amount as approximation
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

    private Page<OrdersDTO> filterWithDateRangeInstructor(String username, String orderType, String status, String startDate, String endDate, Pageable pageable, Long instructorId) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            LocalDateTime endDateTime = end.plusDays(1).atStartOfDay().minusSeconds(1);
            LocalDateTime startDateTime = start.atStartOfDay();
            
            Page<Order> ordersPage = getOrdersWithDateRangeInstructor(username, orderType, status, startDateTime, endDateTime, pageable, instructorId);
            return ordersPage.map(ordersMapper::toInstructorDTO);
        } catch (Exception e) {
            // If date parsing fails, fall back to filtering without date range
            return filterWithoutDateRangeInstructor(username, orderType, status, pageable, instructorId);
        }
    }

    private Page<Order> getOrdersWithDateRangeInstructor(String username, String orderType, String status, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable, Long instructorId) {
        FilterCriteria criteria = new FilterCriteria(username, orderType, status);
        
        switch (criteria.getFilterType()) {
            case ALL_FILTERS:
                return ordersRepository.findByInstructorIdAndUserUsernameContainingAndOrderTypeAndStatusAndOrderDateBetween(
                    instructorId, username, orderType, status, startDateTime, endDateTime, pageable);
            case USERNAME_AND_STATUS:
                return ordersRepository.findByInstructorIdAndUserUsernameContainingAndStatusAndOrderDateBetween(
                    instructorId, username, status, startDateTime, endDateTime, pageable);
            case ORDER_TYPE_AND_STATUS:
                return ordersRepository.findByInstructorIdAndOrderTypeAndStatusAndOrderDateBetween(
                    instructorId, orderType, status, startDateTime, endDateTime, pageable);
            case STATUS_ONLY:
                return ordersRepository.findByInstructorIdAndStatusAndOrderDateBetween(
                    instructorId, status, startDateTime, endDateTime, pageable);
            case USERNAME_AND_ORDER_TYPE:
                return ordersRepository.findByInstructorIdAndUserUsernameContainingAndOrderTypeAndOrderDateBetween(
                    instructorId, username, orderType, startDateTime, endDateTime, pageable);
            case USERNAME_ONLY:
                return ordersRepository.findByInstructorIdAndUserUsernameContainingAndOrderDateBetween(
                    instructorId, username, startDateTime, endDateTime, pageable);
            case ORDER_TYPE_ONLY:
                return ordersRepository.findByInstructorIdAndOrderTypeAndOrderDateBetween(
                    instructorId, orderType, startDateTime, endDateTime, pageable);
            default:
                return ordersRepository.findByInstructorIdAndOrderDateBetween(instructorId, startDateTime, endDateTime, pageable);
        }
    }

    private Page<OrdersDTO> filterWithoutDateRangeInstructor(String username, String orderType, String status, Pageable pageable, Long instructorId) {
        Page<Order> ordersPage = getOrdersWithoutDateRangeInstructor(username, orderType, status, pageable, instructorId);
        return ordersPage.map(ordersMapper::toInstructorDTO);
    }

    private Page<Order> getOrdersWithoutDateRangeInstructor(String username, String orderType, String status, Pageable pageable, Long instructorId) {
        FilterCriteria criteria = new FilterCriteria(username, orderType, status);
        
        switch (criteria.getFilterType()) {
            case ALL_FILTERS:
                return ordersRepository.findByInstructorIdAndUserUsernameContainingAndOrderTypeAndStatus(
                    instructorId, username, orderType, status, pageable);
            case USERNAME_AND_STATUS:
                return ordersRepository.findByInstructorIdAndUserUsernameContainingAndStatus(
                    instructorId, username, status, pageable);
            case ORDER_TYPE_AND_STATUS:
                return ordersRepository.findByInstructorIdAndOrderTypeAndStatus(
                    instructorId, orderType, status, pageable);
            case STATUS_ONLY:
                return ordersRepository.findByInstructorIdAndStatus(instructorId, status, pageable);
            case USERNAME_AND_ORDER_TYPE:
                return ordersRepository.findByInstructorIdAndUserUsernameContainingAndOrderType(
                    instructorId, username, orderType, pageable);
            case USERNAME_ONLY:
                return ordersRepository.findByInstructorIdAndUserUsernameContaining(
                    instructorId, username, pageable);
            case ORDER_TYPE_ONLY:
                return ordersRepository.findByInstructorIdAndOrderType(instructorId, orderType, pageable);
            default:
                return ordersRepository.findByInstructorId(instructorId, pageable);
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
    public Page<InstructorOrderDTO> filterAndSortInstructorOrders(String username, String amountDirection, String orderType, String startDate, String endDate, String status, int page, int size, Long instructorId) {
        Pageable pageable = createInstructorPageable(amountDirection, page, size, instructorId);
        
        if (hasDateRange(startDate, endDate)) {
            return filterWithDateRangeInstructorNew(username, orderType, status, startDate, endDate, pageable, instructorId);
        } else {
            return filterWithoutDateRangeInstructorNew(username, orderType, status, pageable, instructorId);
        }
    }

    @Override
    public List<OrderDetail> getInstructorOrderDetailsByOrderId(Long orderId, Long instructorId) {
        List<OrderDetail> allOrderDetails = orderDetailRepository.findByOrderOrderId(orderId);
        
        // Filter only order details for instructor's courses
        return allOrderDetails.stream()
                .filter(detail -> detail.getCourse() != null && 
                        detail.getCourse().getInstructor() != null && 
                        detail.getCourse().getInstructor().getUserId().equals(instructorId))
                .collect(Collectors.toList());
    }

    private Page<InstructorOrderDTO> filterWithDateRangeInstructorNew(String username, String orderType, String status, String startDate, String endDate, Pageable pageable, Long instructorId) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            LocalDateTime endDateTime = end.plusDays(1).atStartOfDay().minusSeconds(1);
            LocalDateTime startDateTime = start.atStartOfDay();
            
            Page<Order> ordersPage = getOrdersWithDateRangeInstructor(username, orderType, status, startDateTime, endDateTime, pageable, instructorId);
            return ordersPage.map(order -> instructorOrderMapper.toInstructorDTO(order, instructorId));
        } catch (Exception e) {
            // If date parsing fails, fall back to filtering without date range
            return filterWithoutDateRangeInstructorNew(username, orderType, status, pageable, instructorId);
        }
    }

    private Page<InstructorOrderDTO> filterWithoutDateRangeInstructorNew(String username, String orderType, String status, Pageable pageable, Long instructorId) {
        Page<Order> ordersPage = getOrdersWithoutDateRangeInstructor(username, orderType, status, pageable, instructorId);
        return ordersPage.map(order -> instructorOrderMapper.toInstructorDTO(order, instructorId));
    }

    @Override
    public void markOrderAsPaid(Long orderId, String refCode) {
        Optional<Order> optionalOrder = ordersRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if (!"PAID".equals(order.getStatus())) {
                order.setStatus("PAID");
                if (refCode != null) order.setRefCode(refCode);
                ordersRepository.save(order);
                processOrderCompletion(order);
            }
        }
    }

    private void processOrderCompletion(Order order) {
        User user = order.getUser();
        Long userId = user.getUserId();
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
        if (orderDetails == null || orderDetails.isEmpty()) return;

        for (OrderDetail orderDetail : orderDetails) {
            boolean alreadyEnrolled = enrollmentRepository.existsByUser_UserIdAndCourse_CourseId(user.getUserId(), orderDetail.getCourse().getCourseId());
            if (!alreadyEnrolled) {
                Enrollment enrollment = new Enrollment();
                enrollment.setUser(user);
                enrollment.setCourse(orderDetail.getCourse());
                enrollment.setEnrollmentDate(new java.util.Date());
                enrollment.setProgress(0.0);
                enrollment.setStatus("onGoing");
                enrollment.setOrder(order);
                enrollmentRepository.save(enrollment);
            }
        }

        // 2. Cộng/trừ coin cho user và instructor
        Pageable pageable = PageRequest.of(0, 1);
        boolean hasStudentTransaction = coinTransactionRepository.findByUserUserIdAndTransactionTypeAndStatus(user.getUserId(), "course_purchase", "completed", pageable).hasContent();
        if (!hasStudentTransaction) {
            for (OrderDetail orderDetail : orderDetails) {
                double coursePrice = orderDetail.getUnitPrice();
                CoinTransaction studentTransaction = new CoinTransaction();
                studentTransaction.setUser(user);
                studentTransaction.setAmount(java.math.BigDecimal.valueOf(-coursePrice));
                studentTransaction.setStatus("PAID");
                studentTransaction.setCreatedAt(java.time.LocalDateTime.now());
                studentTransaction.setOrder(order);
                studentTransaction.setTransactionType("course_purchase");
                studentTransaction.setNote("Purchase course via SePay");
                coinTransactionRepository.save(studentTransaction);
                user.setCoin(user.getCoin() - (long) coursePrice);

                Notification notificationuser = new Notification();
                notificationuser.setUser(user);
                notificationuser.setCourse(orderDetail.getCourse());
                notificationuser.setMessage("You have purchased a course: " + orderDetail.getCourse().getTitle());
                notificationuser.setSentAt(java.time.LocalDateTime.now());
                notificationuser.setStatus("failed");
                notificationRepository.save(notificationuser);

                User instructor = orderDetail.getCourse().getInstructor();
                if (instructor != null) {
                    CoinTransaction instructorTransaction = new CoinTransaction();
                    instructorTransaction.setUser(instructor);
                    instructorTransaction.setAmount(java.math.BigDecimal.valueOf(coursePrice));
                    instructorTransaction.setStatus("PAID");
                    instructorTransaction.setCreatedAt(java.time.LocalDateTime.now());
                    instructorTransaction.setOrder(null);
                    instructorTransaction.setTransactionType("course_purchase");
                    instructorTransaction.setNote("students buy courses");
                    coinTransactionRepository.save(instructorTransaction);
                    instructor.setCoin(instructor.getCoin() + (long) coursePrice);
                    userRepository.save(instructor);
                }
                Notification notification = new Notification();
                notification.setUser(instructor);
                notification.setCourse(orderDetail.getCourse());
                notification.setMessage("You have received a payment of " + coursePrice + " VND for your course " + orderDetail.getCourse().getTitle());
                notification.setType("PAYMENT_RECEIVED");
                notification.setStatus("failed");
                notification.setSentAt(java.time.LocalDateTime.now());
                notificationRepository.save(notification);
            }
            userRepository.save(user);
        }

        cartService.clearCart(user.getEmail());

        for (OrderDetail orderDetail : orderDetails) {
            try {
                Field voucherField = orderDetail.getClass().getDeclaredField("voucherId");
                voucherField.setAccessible(true);
                Object voucherIdObj = voucherField.get(orderDetail);
                if (voucherIdObj != null) {
                    Long voucherId = (Long) voucherIdObj;
                    if (voucherId != null) {
                        voucherService.useVoucherForUserAndCourse(voucherId, userId);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
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
        order.setStatus("PENDING");
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

    @Override
    public void saveOrderDetail(OrderDetail orderDetail) {
        orderDetailRepository.save(orderDetail);
    }
}
