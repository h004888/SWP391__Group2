package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.user.UserDTO;
import com.OLearning.dto.user.UserDetailDTO;
import com.OLearning.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class UserController {
    @Autowired
    private UserService userService;

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
        model.addAttribute("accNamePage", "Management Account");
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

        //Add pagination information to model
        model.addAttribute("listU", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("roleId", roleId);
        model.addAttribute("status", status);

        return "adminDashBoard/fragments/userTableRowContent :: userTableRows";
    }

    // FIX: New endpoint to get pagination info separately
    @GetMapping("/account/pagination-info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPaginationInfo(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, name = "role") Long roleId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false) Boolean status) {

        if (roleId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("totalPages", 0);
            response.put("totalElements", 0);
            response.put("currentPage", 0);
            response.put("currentElements", 0);
            return ResponseEntity.ok(response);
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

        Map<String, Object> response = new HashMap<>();
        response.put("totalPages", userPage.getTotalPages());
        response.put("totalElements", userPage.getTotalElements());
        response.put("currentPage", page);
        response.put("currentElements", userPage.getNumberOfElements());
        response.put("hasNext", userPage.hasNext());
        response.put("hasPrevious", userPage.hasPrevious());

        return ResponseEntity.ok(response);
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

    @GetMapping("/account/block/{userId}")
    public String blockAccount(Model model, @PathVariable("userId") long id) {
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/accountContent :: accountContent");
        userService.changStatus(id);
        return "redirect:/admin/account";
    }

    @GetMapping("account/resetPass/{userId}")
    public String resetPassword(Model model, @PathVariable("userId") long id,RedirectAttributes redirectAttributes) {
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/accountContent :: accountContent");
        userService.resetPassword(id);
        redirectAttributes.addFlashAttribute("successMessage", "Reset password successfully");
        return "redirect:/admin/account";
    }

    @GetMapping("/account/delete/{userId}")
    public String deleteAccount(Model model, @PathVariable("userId") long id,RedirectAttributes redirectAttributes) {
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/accountContent :: accountContent");
        redirectAttributes.addFlashAttribute("successMessage", "Delete staff successfully");
        userService.deleteAcc(id);
        return "redirect:/admin/account";
    }

}