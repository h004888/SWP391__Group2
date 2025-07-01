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
import com.OLearning.dto.course.CourseDTO;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/vouchers")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private UserRepository userRepository;

    
    @GetMapping
    public String showVoucherPage(@AuthenticationPrincipal UserDetails userDetails, 
                                  Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "6") int size) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        List<UserVoucherDTO> userVouchers = voucherService.getUserVouchersSortedByLatest(userId);
        
        // Tính toán phân trang
        int totalItems = userVouchers.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalItems);
        
        // Lấy items cho trang hiện tại
        List<UserVoucherDTO> pageVouchers = userVouchers.subList(startIndex, endIndex);
        
        model.addAttribute("userVouchers", pageVouchers);
        model.addAttribute("voucherCode", "");
        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("hasNext", page < totalPages - 1);
        model.addAttribute("hasPrevious", page > 0);
        return "homePage/voucher";
    }

    @PostMapping("/apply")
    @ResponseBody
    public Map<String, Object> applyVoucher(@RequestParam("code") String code, @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> result = new HashMap<>();
        if (userDetails == null) {
            result.put("success", false);
            result.put("message", "Bạn cần đăng nhập.");
            return result;
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        try {
            VoucherDTO voucher = voucherService.applyVoucher(code, userId);
            // Tạo UserVoucherDTO trả về cho JS
            UserVoucherDTO userVoucherDTO = new UserVoucherDTO();
            userVoucherDTO.setVoucherId(voucher.getVoucherId());
            userVoucherDTO.setVoucherCode(voucher.getCode());
            userVoucherDTO.setDiscount(voucher.getDiscount());
            userVoucherDTO.setExpiryDate(voucher.getExpiryDate());
            userVoucherDTO.setIsUsed(false);
            result.put("success", true);
            result.put("message", "Áp dụng voucher thành công!");
            result.put("voucher", userVoucherDTO);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
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

    @GetMapping("/voucher/{voucherId}/user/{userId}/courses")
    @ResponseBody
    public List<CourseDTO> getValidCoursesForVoucherAndUser(@PathVariable Long voucherId, @PathVariable Long userId) {
        return voucherService.getValidCoursesForVoucherAndUser(voucherId, userId);
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email))
                .getUserId();
    }
}
