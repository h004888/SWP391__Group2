package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.adminDashBoard.UserDTO;
import com.OLearning.dto.adminDashBoard.UserDetailDTO;
import com.OLearning.entity.User;
import com.OLearning.service.adminDashBoard.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    public String getAdminDashboardPAge(Model model) {
        model.addAttribute("fragmentContent", "adminDashboard/fragments/content :: contentMain");
        return "adminDashboard/index";
    }


    @GetMapping("/account")
    public String getAccountPage(Model model) {
        model.addAttribute("fragmentContent", "adminDashboard/fragments/accountContent :: accountContent");
        model.addAttribute("listU", userService.getAllUsers());
        model.addAttribute("accNamePage", "Management Account");
        model.addAttribute("addAccount", new UserDTO());
        model.addAttribute("listRole", userService.getListRole());
        return "adminDashboard/index";
    }

    //Filter with ajax
    @GetMapping("/account/filter")
    public String filterAccounts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, name = "role") Integer roleId,
            Model model) {
        List<UserDTO> listU = userService.searchByName(keyword, roleId);
        model.addAttribute("listU", listU);
        return "adminDashboard/fragments/accountContent :: userTableBody";
    }

    @PostMapping("/account/add")
    public String addUser(@ModelAttribute("addAccount") UserDTO userDTO, RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(userService.userDTOtoUser(userDTO));
            redirectAttributes.addFlashAttribute("successMessage", "User added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding user: " + e.getMessage());
        }
        return "redirect:/admin/account";
    }

    @GetMapping("account/viewInfo/{userId}")
    public String getDetailAccount(Model model, @PathVariable("userId") long id) {
        model.addAttribute("fragmentContent", "adminDashboard/fragments/accountDetailContent :: accountDetail");
        Optional<UserDetailDTO> userDetailDTO = userService.getInfoUser(id);
        if (userDetailDTO.isPresent()) {
            model.addAttribute("accNamePage","Detail Account");
            model.addAttribute("userDetail", userDetailDTO.get());
            return "adminDashboard/index";
        } else {
            return "redirect:/admin/index";
        }
    }

    @GetMapping("admin/account/delete/{userId}")
    public String deleteAccount(Model model,@PathVariable("userId") long id) {
        model.addAttribute("fragmentContent", "adminDashboard/fragments/accountContent :: accountContent");
        userService.deleteAcc(id);
        return "redirect:/admin/index";
    }


}
