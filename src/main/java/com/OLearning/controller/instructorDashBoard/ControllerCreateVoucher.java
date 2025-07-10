package com.OLearning.controller.instructorDashBoard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.OLearning.dto.voucher.VoucherDTO;
import com.OLearning.entity.User;
import com.OLearning.entity.Course;
import com.OLearning.repository.UserRepository;
import com.OLearning.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import com.OLearning.service.voucher.VoucherService;
import com.OLearning.dto.voucher.VoucherStatsDTO;
import java.util.Map;
import java.util.HashMap;
import com.OLearning.entity.Voucher;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

@Controller
@RequestMapping("/instructordashboard/voucher")
public class ControllerCreateVoucher {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private VoucherService voucherService;

    @GetMapping()
    public String manageVouchers(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam(value = "search", required = false) String search,
                                 Model model) {
        if (userDetails == null) return "redirect:/login";
        String email = userDetails.getUsername();
        User instructor = userRepository.findByEmail(email).orElse(null);
        if (instructor == null) return "redirect:/login";

        VoucherStatsDTO stats = voucherService.getVoucherStatsForInstructor(instructor.getUserId(), search);
        List<Course> courses = courseRepository.findByInstructorUserId(instructor.getUserId());
        
        model.addAttribute("stats", stats);
        model.addAttribute("courses", courses);
        model.addAttribute("voucherDTO", new VoucherDTO());
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/voucherContent :: voucherContent");
        return "instructorDashboard/indexUpdate";
    }

    @PostMapping("/create")
    public String createVoucher(@ModelAttribute @Valid VoucherDTO voucherDTO,
                               BindingResult bindingResult, // Đặt ngay sau @Valid
                               @AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam(value = "selectedCourses", required = false) List<Long> selectedCourses,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (userDetails == null) return "redirect:/login";
        String email = userDetails.getUsername();
        User instructor = userRepository.findByEmail(email).orElse(null);
        if (instructor == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            VoucherStatsDTO stats = voucherService.getVoucherStatsForInstructor(instructor.getUserId(), null);
            List<Course> courses = courseRepository.findByInstructorUserId(instructor.getUserId());
            model.addAttribute("stats", stats);
            model.addAttribute("courses", courses);
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/voucherContent :: voucherContent");
            return "instructorDashboard/indexUpdate";
        }

        try {
            voucherService.createVoucherForInstructor(voucherDTO, instructor.getUserId(), selectedCourses);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo voucher thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi không mong muốn: " + e.getMessage());
        }
        
        return "redirect:/instructordashboard/voucher";
    }

    @GetMapping("/view-courses/{voucherId}")
    @ResponseBody
    public List<String> getValidCoursesForVoucher(@PathVariable Long voucherId) {
        return voucherService.getValidCourseTitlesForVoucher(voucherId);
    }

    // Filter with AJAX - returns HTML fragment
    @GetMapping("/filter")
    public String filterVouchers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "valid") String status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Model model) {

        if (userDetails == null) {
            model.addAttribute("vouchers", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            return "instructorDashboard/fragments/voucherTableRows :: voucherTableRows";
        }

        String email = userDetails.getUsername();
        User instructor = userRepository.findByEmail(email).orElse(null);
        if (instructor == null) {
            model.addAttribute("vouchers", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            return "instructorDashboard/fragments/voucherTableRows :: voucherTableRows";
        }

        Page<VoucherDTO> voucherPage = voucherService.getVouchersForInstructor(
            instructor.getUserId(), keyword, status, page, size);

        model.addAttribute("vouchers", voucherPage.getContent());
        model.addAttribute("currentPage", voucherPage.getNumber());
        model.addAttribute("totalPages", voucherPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "instructorDashboard/fragments/voucherTableRows :: voucherTableRows";
    }

    // Get pagination fragment
    @GetMapping("/pagination")
    public String getPaginationFragment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "valid") String tabType,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Model model) {

        if (userDetails == null) {
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0L);
            return "instructorDashboard/fragments/voucherPagination :: voucherPagination";
        }

        String email = userDetails.getUsername();
        User instructor = userRepository.findByEmail(email).orElse(null);
        if (instructor == null) {
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0L);
            return "instructorDashboard/fragments/voucherPagination :: voucherPagination";
        }

        // Get pagination info from service
        Map<String, Object> result = voucherService.getFilteredVouchersForInstructor(
            instructor.getUserId(), keyword, tabType, page, size);

        if ((Boolean) result.get("success")) {
            Map<String, Object> pagination = (Map<String, Object>) result.get("pagination");

            model.addAttribute("currentPage", pagination.get("currentPage"));
            model.addAttribute("totalPages", pagination.get("totalPages"));
            model.addAttribute("totalItems", pagination.get("totalItems"));
            model.addAttribute("hasNext", pagination.get("hasNext"));
            model.addAttribute("hasPrevious", pagination.get("hasPrevious"));
            model.addAttribute("keyword", keyword);
            model.addAttribute("tabType", tabType);
        } else {
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0L);
        }

        return "instructorDashboard/fragments/voucherPagination :: voucherPagination";
    }
}
