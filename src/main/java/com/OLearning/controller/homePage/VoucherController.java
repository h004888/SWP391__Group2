package com.OLearning.controller.homePage;


import com.OLearning.dto.voucher.UserVoucherDTO;
import com.OLearning.dto.voucher.VoucherDTO;
import com.OLearning.service.voucher.VoucherService;
import com.OLearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Controller
@RequestMapping("/vouchers")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private UserRepository userRepository;

    
    @GetMapping
    public String showVoucherPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        List<UserVoucherDTO> userVouchers = voucherService.getUserVouchers(userId);
        model.addAttribute("userVouchers", userVouchers);
        model.addAttribute("voucherCode", "");
        model.addAttribute("currentUserId", userId);
        return "homePage/voucher";
    }

    @PostMapping("/apply")
    public String applyVoucher(@RequestParam("code") String code, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        try {
            VoucherDTO voucher = voucherService.applyVoucher(code, userId);
            model.addAttribute("successMessage", "Voucher applied successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        List<UserVoucherDTO> userVouchers = voucherService.getUserVouchers(userId);
        model.addAttribute("userVouchers", userVouchers);
        model.addAttribute("voucherCode", code);
        return "homePage/voucher";
    }

    @GetMapping("/course/{courseId}")
    @ResponseBody
    public List<VoucherDTO> getValidVouchersForCourse(@PathVariable Long courseId) {
        return voucherService.getValidVouchersForCourse(courseId);
    }

    @PostMapping("/use")
    public String useVoucher(@RequestParam("voucherId") Long voucherId,
                             @RequestParam("courseId") Long courseId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        try {
            VoucherDTO voucher = voucherService.useVoucher(voucherId, userId, courseId);
            model.addAttribute("successMessage", "Voucher used successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/courses/" + courseId;
    }

    @GetMapping("/course/{courseId}/user/{userId}")
    @ResponseBody
    public List<UserVoucherDTO> getValidVouchersForCourseAndUser(@PathVariable Long courseId, @PathVariable Long userId) {
        List<UserVoucherDTO> result = voucherService.getValidVouchersForCourseAndUser(courseId, userId);
        return result;
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email))
                .getUserId();
    }
}
