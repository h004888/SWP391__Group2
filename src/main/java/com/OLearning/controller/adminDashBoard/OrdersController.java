package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.adminDashBoard.OrdersDTO;
import com.OLearning.service.adminDashBoard.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class OrdersController {

    @Autowired
    private OrdersService orderService;

    @GetMapping
    public String getAllOrders(Model model) {
        List<OrdersDTO> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/ordersContent :: contentOrders");
        return "adminDashBoard/index";
    }

    @GetMapping("/searchByUsername")
    public String searchByUsername(@RequestParam(value = "username", required = false) String username, Model model) {
        List<OrdersDTO> orders = orderService.findByUsername(username);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/ordersContent :: contentOrders");
        model.addAttribute("orders", orders);
        model.addAttribute("username", username);
        return "adminDashBoard/index";
    }

    @GetMapping("/searchByCourseName")
    public String searchByCourseName(@RequestParam(value = "courseName", required = false) String courseName, Model model) {
        List<OrdersDTO> orders = orderService.findByCourseName(courseName);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/ordersContent :: contentOrders");
        model.addAttribute("orders", orders);
        model.addAttribute("courseName", courseName);
        return "adminDashBoard/index";
    }

    @GetMapping("/sortByAmount")
    public String sortByAmount(@RequestParam(value = "direction", required = false) String direction, Model model) {
        model.addAttribute("amountDirection", direction);
        List<OrdersDTO> orders = orderService.sortByAmount(direction);
        model.addAttribute("orders", orders);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/ordersContent :: contentOrders");
        return "adminDashBoard/index";
    }

    @GetMapping("/sortByDate")
    public String sortByDate(@RequestParam(value = "direction", required = false) String direction, Model model) {
        List<OrdersDTO> orders = orderService.sortByDate(direction);
        model.addAttribute("orders", orders);
        model.addAttribute("dateDirection", direction);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/ordersContent :: contentOrders");
        return "adminDashBoard/index";
    }
}