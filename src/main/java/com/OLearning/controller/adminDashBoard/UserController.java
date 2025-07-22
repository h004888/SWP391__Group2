package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.enrollment.EnrollmentDTO;
import com.OLearning.dto.user.UserDTO;
import com.OLearning.dto.user.UserDetailDTO;
import com.OLearning.entity.Notification;
import com.OLearning.entity.User;
import com.OLearning.service.enrollment.EnrollmentService;
import com.OLearning.service.notification.NotificationService;
import com.OLearning.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class UserController {
    private static final String ACC_NAME_PAGE_MANAGEMENT = "Management Account";
    private static final String SUCCESS_MESSAGE_DELETE_STAFF = "Delete staff successfully";

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/account")
    public String getAccountPage(
            Model model,
            @RequestParam(required = false,defaultValue = "0") int page) {

        Pageable prs = PageRequest.of(page, 5);
        Page<UserDTO> listU = userService.getAllUsers(prs);
        List<UserDTO> listUser = listU.getContent();

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", listU.getTotalPages());
        model.addAttribute("totalItems", listU.getTotalElements());

        model.addAttribute("fragmentContent", "adminDashBoard/fragments/accountContent :: accountContent");
        model.addAttribute("listU", listUser);
        model.addAttribute("accNamePage", ACC_NAME_PAGE_MANAGEMENT);
        model.addAttribute("addAccount", new UserDTO());
        model.addAttribute("listRole", userService.getListRole());
        return "adminDashBoard/index";
    }

    @PostMapping("/account/add")
    public String addUser(@ModelAttribute("addAccount") UserDTO userDTO, RedirectAttributes redirectAttributes) {
        try {
            userService.createNewStaff(userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "User added successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error: " + e.getMessage());
        }
        return "redirect:/admin/account";
    }

    //Filter with ajax
    @GetMapping("/account/filter")
    public String filterAccounts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, name = "role") Long roleId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false) Boolean status,
            Model model) {

        if (roleId == null) {
            model.addAttribute("listU", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0);
            return "adminDashBoard/fragments/userTableRowContent :: userTableRows";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> userPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status != null) {
                userPage = userService.searchByNameAndStatusWithPagination(keyword.trim(), roleId, status, pageable);
            } else {
                userPage = userService.searchByNameWithPagination(keyword.trim(), roleId, pageable);
            }
        } else {
            if (status != null) {
                userPage = userService.getUsersByRoleAndStatusWithPagination(roleId, status, pageable);
            } else {
                userPage = userService.getUsersByRoleWithPagination(roleId, pageable);
            }
        }

        model.addAttribute("listU", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("roleId", roleId);
        model.addAttribute("status", status);

        return "adminDashBoard/fragments/userTableRowContent :: userTableRows";
    }

    @GetMapping("/account/pagination")
    public String getPaginationFragment(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, name = "role") Long roleId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false) Boolean status,
            Model model) {

        if (roleId == null) {
            model.addAttribute("listU", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0L);
            return "adminDashBoard/fragments/userTableRowContent :: accountPagination";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> userPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status != null) {
                userPage = userService.searchByNameAndStatusWithPagination(keyword.trim(), roleId, status, pageable);
            } else {
                userPage = userService.searchByNameWithPagination(keyword.trim(), roleId, pageable);
            }
        } else {
            if (status != null) {
                userPage = userService.getUsersByRoleAndStatusWithPagination(roleId, status, pageable);
            } else {
                userPage = userService.getUsersByRoleWithPagination(roleId, pageable);
            }
        }

        model.addAttribute("listU", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());

        return "adminDashBoard/fragments/userTableRowContent :: accountPagination";
    }

    @GetMapping("/account/viewInfo/{userId}")
    public String getDetailAccount(Model model, @PathVariable("userId") long id) {
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/accountDetailContent :: accountDetail");
        Optional<UserDetailDTO> userDetailDTO = userService.getInfoUser(id);
        if (userDetailDTO.isPresent()) {
            model.addAttribute("accNamePage", "Detail Account");
            model.addAttribute("userDetail", userDetailDTO.get());
            return "adminDashBoard/index";
        } else {
            return "redirect:/admin/account";
        }
    }
    @GetMapping("/account/viewInfo/{userId}/pagingEnrolledCourse")
    public String pagingEnrolledCourses(
            @PathVariable("userId") long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        Optional<UserDetailDTO> userDetailDTO = userService.getInfoUser(userId);
        if (userDetailDTO.isPresent()) {
            UserDetailDTO userDetail = userDetailDTO.get();
            userDetail.getEnrolledCourses();
            
        }

        return "adminDashBoard/fragments/accountDetailContent :: enrolledCourseListFragment";
    }

    @GetMapping("/account/counts")
    @ResponseBody
    public Map<String, Long> getAccountCounts(
            @RequestParam(required = false) String keyword) {
        Map<String, Long> counts = new HashMap<>();
        for (long roleId = 1; roleId <= 3; roleId++) {
            Pageable pageable = PageRequest.of(0, 1);
            // Active
            Page<UserDTO> activePage;
            if (keyword != null && !keyword.trim().isEmpty()) {
                activePage = userService.searchByNameAndStatusWithPagination(keyword.trim(), roleId, true, pageable);
            } else {
                activePage = userService.getUsersByRoleAndStatusWithPagination(roleId, true, pageable);
            }
            // Inactive
            Page<UserDTO> inactivePage;
            if (keyword != null && !keyword.trim().isEmpty()) {
                inactivePage = userService.searchByNameAndStatusWithPagination(keyword.trim(), roleId, false, pageable);
            } else {
                inactivePage = userService.getUsersByRoleAndStatusWithPagination(roleId, false, pageable);
            }
            String roleKey = roleId == 1 ? "admin" : roleId == 2 ? "instructor" : "user";
            counts.put(roleKey + "Active", activePage.getTotalElements());
            counts.put(roleKey + "Inactive", inactivePage.getTotalElements());
        }
        return counts;
    }

    @PostMapping("/account/block/{userId}")
    public String blockAccount(Model model, @PathVariable("userId") long id, @RequestParam(value = "reason", required = false) String reason, RedirectAttributes redirectAttributes) {
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/accountContent :: accountContent");
         userService.changStatus(id, reason);
        return "redirect:/admin/account";
    }

    @GetMapping("account/resetPass/{userId}")
    public String resetPassword(Model model, @PathVariable("userId") long id,RedirectAttributes redirectAttributes) {
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/accountContent :: accountContent");
        userService.resetPassword(id);
        redirectAttributes.addFlashAttribute("successMessage", "Reset password successfully");
        return "redirect:/admin/account";
    }

    @PostMapping("/account/delete/{userId}")
    public String deleteAccount(Model model, @PathVariable("userId") long id,RedirectAttributes redirectAttributes) {
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/accountContent :: accountContent");
        redirectAttributes.addFlashAttribute("successMessage", SUCCESS_MESSAGE_DELETE_STAFF);
        userService.deleteAcc(id);
        return "redirect:/admin/account";
    }



}
