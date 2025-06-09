package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.order.OrdersDTO;
import com.OLearning.entity.OrderDetail;
import com.OLearning.service.order.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @GetMapping
    public String getAllOrders(Model model,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "5") int size,
                               @RequestParam(value = "username", required = false) String username,
                               @RequestParam(value = "amountDirection", required = false) String amountDirection,
                               @RequestParam(value = "dateDirection", required = false) String dateDirection) {
        Page<OrdersDTO> ordersPage = ordersService.filterAndSortOrders(username, amountDirection, dateDirection, page, size);
        model.addAttribute("accNamePage", "Management Orders");
        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("totalItems", ordersPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("username", username);
        model.addAttribute("amountDirection", amountDirection);
        model.addAttribute("dateDirection", dateDirection);
        model.addAttribute("fragmentContent", "adminDashboard/fragments/ordersContent :: contentOrders");
        return "adminDashboard/index";
    }

    @GetMapping("/filter")
    public String filterOrders(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "amountDirection", required = false) String amountDirection,
            @RequestParam(value = "dateDirection", required = false) String dateDirection,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {
        Page<OrdersDTO> ordersPage = ordersService.filterAndSortOrders(username, amountDirection, dateDirection, page, size);
        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("totalItems", ordersPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("username", username);
        model.addAttribute("amountDirection", amountDirection);
        model.addAttribute("dateDirection", dateDirection);
        return "adminDashboard/fragments/ordersContent :: ordersTableBody";
    }

    @GetMapping("/view/{orderId}")
    public String viewOrderDetails(@PathVariable("orderId") Long orderId, Model model) {
        model.addAttribute("accNamePage", "Order Details");
        List<OrderDetail> orderDetails = ordersService.getOrderDetailsByOrderId(orderId);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("orderId", orderId);
        model.addAttribute("fragmentContent", "adminDashboard/fragments/orderDetailsContent :: contentOrderDetails");
        return "adminDashboard/index";
    }}