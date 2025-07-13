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
import com.OLearning.repository.CourseRepository;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/vouchers")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;
    
    @GetMapping
    public String showVoucherPage(@AuthenticationPrincipal UserDetails userDetails, 
                                  Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "6") int size) {
        Long userId = userDetails != null ? getUserIdFromUserDetails(userDetails) : 0L;
        List<UserVoucherDTO> userVouchers = userDetails != null ? voucherService.getUserVouchersSortedByLatest(userId) : List.of();
        List<VoucherDTO> publicVouchers = voucherService.getPublicVouchers();

        // Filter out public vouchers that the user already has
        List<VoucherDTO> filteredPublicVouchers = publicVouchers.stream()
                .filter(publicVoucher -> userVouchers.stream()
                        .noneMatch(userVoucher -> userVoucher.getVoucherId().equals(publicVoucher.getVoucherId())))
                .collect(Collectors.toList());
        
        // Tính toán phân trang
        int totalItems = userVouchers.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalItems);
        
        // Lấy items cho trang hiện tại
        List<UserVoucherDTO> pageVouchers = userVouchers.isEmpty() ? List.of() : userVouchers.subList(startIndex, endIndex);
        
        model.addAttribute("userVouchers", pageVouchers);
        model.addAttribute("voucherCode", "");
        model.addAttribute("currentUserId", userId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("hasNext", page < totalPages - 1);
        model.addAttribute("hasPrevious", page > 0);
        model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
        model.addAttribute("fragmentContent", "homePage/fragments/voucherContent :: voucherContent");
        model.addAttribute("publicVouchers", filteredPublicVouchers);
        return "homePage/index";
    }

    @PostMapping("/apply")
    @ResponseBody
    public Map<String, Object> applyVoucher(@RequestParam("code") String code, @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> result = new HashMap<>();
        if (userDetails == null) {
            result.put("success", false);
            result.put("message", "You need to login.");
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
            result.put("message", "Voucher applied successfully!");
            result.put("voucher", userVoucherDTO);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/claim/{voucherId}")
    @ResponseBody
    public Map<String, Object> claimPublicVoucher(@PathVariable Long voucherId, @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> result = new HashMap<>();
        if (userDetails == null) {
            result.put("success", false);
            result.put("message", "You need to login to receive voucher.");
            return result;
        }
        Long userId = getUserIdFromUserDetails(userDetails);
        try {
            voucherService.claimPublicVoucher(voucherId, userId);
            result.put("success", true);
            result.put("message", "Received voucher successfully!");
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
        try {
            // Kiểm tra user có tồn tại không
            if (!userRepository.existsById(userId)) {
                return List.of();
            }
            
            // Kiểm tra course có tồn tại không
            if (!courseRepository.existsById(courseId)) {
                return List.of();
            }
            
            List<UserVoucherDTO> result = voucherService.getValidVouchersForCourseAndUser(courseId, userId);
            return result;
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/voucher/{voucherId}/user/{userId}/courses")
    @ResponseBody
    public List<CourseDTO> getValidCoursesForVoucherAndUser(@PathVariable Long voucherId, @PathVariable Long userId) {
        return voucherService.getValidCoursesForVoucherAndUser(voucherId, userId);
    }

    @GetMapping("/voucher/{voucherId}/courses")
    @ResponseBody
    public List<CourseDTO> getCoursesForVoucher(@PathVariable Long voucherId) {
        return voucherService.getCoursesForVoucher(voucherId);
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email))
                .getUserId();
    }
}
