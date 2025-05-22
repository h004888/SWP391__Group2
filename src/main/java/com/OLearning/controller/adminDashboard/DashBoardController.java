package com.OLearning.controller.adminDashboard;

import com.OLearning.entity.Payment;
import com.OLearning.service.adminDashBoard.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DashBoardController {

    @Autowired
    private PaymentService paymentService;

    public DashBoardController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    // Lấy tất cả payments (mặc định)
    @GetMapping("admin/payments")
    public String getAllPayments(Model model) {
        List<Payment> paymentList = paymentService.getAllPayments();
        model.addAttribute("payments", paymentList);
        return "adminDashboard/utilities-border";
    }

    // Tìm kiếm theo User
    @GetMapping("admin/get-payments-by-user")
    public String getPaymentsByUser(
            @RequestParam(required = false) String username,
            Model model) {
        List<Payment> payments = paymentService.findByUserName(username);
        model.addAttribute("payments", payments);
        return "adminDashboard/utilities-border";
    }

    // Tìm kiếm theo Status
    @GetMapping("admin/get-payments-by-status")
    public String getPaymentsByStatus(
            @RequestParam(required = false) String status,
            Model model) {
        List<Payment> payments = paymentService.findByStatus(status);
        model.addAttribute("payments", payments);
        return "adminDashboard/utilities-border";
    }

    // Sắp xếp theo ngày (tăng dần)
    @GetMapping("admin/get-payments-by-date-asc")
    public String getPaymentsByDateAsc(Model model) {
        List<Payment> payments = paymentService.findAllByDateAsc();
        model.addAttribute("payments", payments);
        return "adminDashboard/utilities-border";
    }

    // Sắp xếp theo ngày (giảm dần)
    @GetMapping("admin/get-payments-by-date-desc")
    public String getPaymentsByDateDesc(Model model) {
        List<Payment> payments = paymentService.findAllByDateDesc();
        model.addAttribute("payments", payments);
        return "adminDashboard/utilities-border";
    }

    // Sắp xếp theo giá (tăng dần)
    @GetMapping("admin/get-payments-by-amount-asc")
    public String getPaymentsByAmountAsc(Model model) {
        List<Payment> payments = paymentService.findAllByAmountAsc();
        model.addAttribute("payments", payments);
        return "adminDashboard/utilities-border";
    }

    // Sắp xếp theo giá (giảm dần)
    @GetMapping("admin/get-payments-by-amount-desc")
    public String getPaymentsByAmountDesc(Model model) {
        List<Payment> payments = paymentService.findAllByAmountDesc();
        model.addAttribute("payments", payments);
        return "adminDashboard/utilities-border";
    }


    @GetMapping("/utilities-other.html")
    public String getOther(){
        return "adminDashboard/utilities-other";
    }
}
