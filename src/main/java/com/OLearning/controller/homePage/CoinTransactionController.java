package com.OLearning.controller.homePage;

import com.OLearning.dto.coinTransaction.CoinTransactionDTO;
import com.OLearning.entity.User;
import com.OLearning.repository.UserRepository;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.coinTransaction.CoinTransactionService;
import com.OLearning.service.payment.VNPayService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;

@Controller
@RequestMapping("/history")
public class CoinTransactionController {

    @Autowired
    private CoinTransactionService coinTransactionService;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String getMyCoursePurchaseHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(value = "courseName", required = false) String courseName,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Model model) {
        if (userDetails == null || !(userDetails instanceof CustomUserDetails)) {
            return "redirect:/login";
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getUserId();
        Page<CoinTransactionDTO> transactionPage = coinTransactionService.getUserCoursePurchaseTransactions(userId, courseName, status, startDate, endDate, page, size);
        User user = userRepository.findById(userId).orElse(null);
        model.addAttribute("transactions", transactionPage.getContent());
        model.addAttribute("currentPage", transactionPage.getNumber());
        model.addAttribute("totalPages", transactionPage.getTotalPages());
        model.addAttribute("totalItems", transactionPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("currentBalance", user != null ? user.getCoin() : 0);
        model.addAttribute("totalSpent", coinTransactionService.getTotalSpent(userId));
        model.addAttribute("totalCoursesPurchased", coinTransactionService.getTotalCoursesPurchased(userId));
        model.addAttribute("courseName", courseName);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
        model.addAttribute("fragmentContent", "homePage/fragments/historyContent :: historyContent");
        return "homePage/index";
    }

    @PostMapping("/deposit")
    public String deposit(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer predefinedAmount,
            @RequestParam(required = false) Integer customAmount,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            // Validate amount - either predefined or custom, but not both
            if ((predefinedAmount == null && customAmount == null) ||
                    (predefinedAmount != null && customAmount != null)) {
                redirectAttributes.addFlashAttribute("error", "Please select or enter deposit amount!");
                return "redirect:/history";
            }

            int amount = predefinedAmount != null ? predefinedAmount : customAmount;
            
            // Validate minimum amount
            if (amount < 10000) {
                redirectAttributes.addFlashAttribute("error", "Minimum deposit amount is 10,000 VND");
                return "redirect:/history";
            }

            if (amount > 10000000) {
                redirectAttributes.addFlashAttribute("error", "Maximum deposit amount is 10,000,000 VND");
                return "redirect:/history";
            }

            request.setAttribute("amount", amount*100);
            String currentPath = request.getRequestURI();
            String basePath = currentPath.substring(0, currentPath.lastIndexOf('/'));
            String returnUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(basePath)
                    .build()
                    .toUriString();
            request.setAttribute("urlReturn", returnUrl);
            String paymentUrl = vnPayService.createOrder(request);
            if (paymentUrl == null || paymentUrl.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Unable to generate payment link");
                return "redirect:/history";
            }
            return "redirect:" + paymentUrl;

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Deposit failed: " + e.getMessage());
            return "redirect:/history";
        }
    }

    @GetMapping("/vnpay-payment-return")
    public String depositReturn(
            HttpServletRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        int paymentStatus = vnPayService.orderReturn(request);
        String transactionId = request.getParameter("vnp_TransactionNo");
        String amountParam = request.getParameter("vnp_Amount");
        int amount = Integer.parseInt(amountParam) / 100;

        if (paymentStatus == 1) {
            try {
                coinTransactionService.saveDepositTransactionAfterPayment(
                        ((CustomUserDetails) userDetails).getUserId(),
                        BigDecimal.valueOf(amount),
                        transactionId
                );
                redirectAttributes.addFlashAttribute("message", "Deposit successful!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error when recording deposit");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Payment failed or was cancelled");
        }

        return "redirect:/history";
    }


    @PostMapping("/withdraw")
    public String withdraw(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer predefinedWithdrawAmount,
            @RequestParam(required = false) Integer customWithdrawAmount,
            RedirectAttributes redirectAttributes) {


        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "User is not logged in");
            return "redirect:/login";
        }

        try {
            Integer amount = null;
            if (predefinedWithdrawAmount != null) {
                amount = predefinedWithdrawAmount;
            } else if (customWithdrawAmount != null) {
                amount = customWithdrawAmount;
            }

            if (amount == null) {
                redirectAttributes.addFlashAttribute("error", "Please enter withdrawal amount!");
                return "redirect:/history";
            }

            if (amount < 10000) {
                redirectAttributes.addFlashAttribute("error", "Minimum withdrawal amount is 10,000 VND");
                return "redirect:/history";
            }

            if (amount > 10000000) {
                redirectAttributes.addFlashAttribute("error", "Maximum withdrawal amount is 10,000,000 VND");
                return "redirect:/history";
            }

            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            Long userId = customUserDetails.getUserId();

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            if (user.getCoin() < amount) {
                redirectAttributes.addFlashAttribute("error",
                        "Insufficient balance to make transaction. Current balance: " +
                                String.format("%,d", user.getCoin()) + " VND");
                return "redirect:/history";
            }

            coinTransactionService.processWithdrawal(userId, BigDecimal.valueOf(amount));
            redirectAttributes.addFlashAttribute("message",
                    "Withdrawal successful!");

            return "redirect:/history";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "An error occurred while withdrawing funds: " + e.getMessage());
            return "redirect:/history";
        }
    }

    // API lấy chi tiết giao dịch cho modal (AJAX)
    @GetMapping("/detail/{id}")
    @ResponseBody
    public CoinTransactionDTO getTransactionDetail(@PathVariable("id") Long transactionId) {
        return coinTransactionService.getTransactionDetail(transactionId);
    }
}