package com.OLearning.controller.homePage;

import com.OLearning.config.VNPayConfig;
import com.OLearning.dto.coinTransaction.CoinTransactionDTO;
import com.OLearning.entity.User;
import com.OLearning.repository.UserRepository;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.coinTransaction.CoinTransactionService;
import com.OLearning.service.vnpay.VNPayService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public String getMyCoinTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "transactionType", required = false) String transactionType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        if (!(userDetails instanceof CustomUserDetails)) {
            model.addAttribute("error", "Invalid authentication");
            return "redirect:/login";
        }

        try {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

            Page<CoinTransactionDTO> transactionPage;
            // Use filter method if any filter is applied
            if ((transactionType != null && !transactionType.trim().isEmpty()) ||
                    (startDate != null && !startDate.trim().isEmpty()) ||
                    (endDate != null && !endDate.trim().isEmpty())) {
                transactionPage = coinTransactionService.filterAndSortTransactions(
                        customUserDetails.getUserId(), transactionType, startDate, endDate, page, size);
            } else {
                Pageable pageable = PageRequest.of(page, size);
                transactionPage = coinTransactionService.getUserCoinTransactions(customUserDetails.getUserId(), pageable);
            }

            User user = userRepository.findById(customUserDetails.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            model.addAttribute("transactions", transactionPage.getContent());
            model.addAttribute("currentPage", transactionPage.getNumber());
            model.addAttribute("totalPages", transactionPage.getTotalPages());
            model.addAttribute("totalItems", transactionPage.getTotalElements());
            model.addAttribute("pageSize", size);
            model.addAttribute("currentBalance", user.getCoin());
            model.addAttribute("predefinedAmounts", new int[]{10000, 20000, 50000, 100000, 200000, 500000});

            // Add filter parameters to model
            model.addAttribute("transactionType", transactionType);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);

            return "homePage/history";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while fetching transactions");
            return "homePage/history";
        }
    }

    @GetMapping("/filter")
    public String filterTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "transactionType", required = false) String transactionType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        if (userDetails == null || !(userDetails instanceof CustomUserDetails)) {
            return "redirect:/login";
        }

        try {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            Page<CoinTransactionDTO> transactionPage;
            
            // Check if any filter is applied
            if ((transactionType != null && !transactionType.trim().isEmpty()) ||
                    (startDate != null && !startDate.trim().isEmpty()) ||
                    (endDate != null && !endDate.trim().isEmpty())) {
                transactionPage = coinTransactionService.filterAndSortTransactions(
                        customUserDetails.getUserId(), transactionType, startDate, endDate, page, size);
            } else {
                // If no filters are applied, get all transactions
                Pageable pageable = PageRequest.of(page, size);
                transactionPage = coinTransactionService.getUserCoinTransactions(customUserDetails.getUserId(), pageable);
            }

            User user = userRepository.findById(customUserDetails.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            model.addAttribute("transactions", transactionPage.getContent());
            model.addAttribute("currentPage", transactionPage.getNumber());
            model.addAttribute("totalPages", transactionPage.getTotalPages());
            model.addAttribute("totalItems", transactionPage.getTotalElements());
            model.addAttribute("pageSize", size);
            model.addAttribute("currentBalance", user.getCoin());
            model.addAttribute("transactionType", transactionType);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);

            return "homePage/history :: transactionsTableBody";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while filtering transactions");
            return "homePage/history";
        }
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
                // Chỉ tạo và lưu giao dịch khi thanh toán thành công
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
            // Validate amount - either predefined or custom, but not both
            if ((predefinedWithdrawAmount == null && customWithdrawAmount == null) ||
                    (predefinedWithdrawAmount != null && customWithdrawAmount != null)) {
                redirectAttributes.addFlashAttribute("error", "Please select or enter the amount you want to withdraw!");
                return "redirect:/history";
            }

            int amount = predefinedWithdrawAmount != null ? predefinedWithdrawAmount : customWithdrawAmount;
            
            // Validate minimum amount
            if (amount < 10000) {
                redirectAttributes.addFlashAttribute("error", "Minimum withdrawal amount is 10,000 VND");
                return "redirect:/history";
            }

            // Validate maximum amount (optional - add if needed)
            if (amount > 10000000) { // 10 million VND
                redirectAttributes.addFlashAttribute("error", "Maximum withdrawal amount is 10,000,000 VND");
                return "redirect:/history";
            }

            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            Long userId = customUserDetails.getUserId();

            // Check current balance
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            if (user.getCoin() < amount) {
                redirectAttributes.addFlashAttribute("error", "Insufficient balance to make transaction. Current balance: " + 
                    String.format("%,.0f", user.getCoin()) + " VND");
                return "redirect:/history";
            }

            // Process withdrawal
            coinTransactionService.processWithdrawal(userId, BigDecimal.valueOf(amount));

            redirectAttributes.addFlashAttribute("message", "Withdrawal successful! Amount: " + 
                String.format("%,.0f", amount) + " VND");
            return "redirect:/history";

        } catch (ClassCastException e) {
            redirectAttributes.addFlashAttribute("error", "Authentication error");
            return "redirect:/history";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/history";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/history";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while withdrawing funds: " + e.getMessage());
            return "redirect:/history";
        }
    }
}