package com.OLearning.controller.adminDashboard;

import com.OLearning.dto.adminDashBoard.UserDTO;
import com.OLearning.dto.adminDashBoard.UserDetailDTO;
import com.OLearning.service.adminDashBoard.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/admin")
    public String getAdminDashboardPAge(Model model) {
        model.addAttribute("fragmentContent", "adminDashboard/fragments/content :: contentMain");
        return "adminDashboard/index";
    }

    @GetMapping("/admin/account")
    public String getAccountPage(Model model) {
        List<UserDTO> listU = userService.getAllUsers();
        model.addAttribute("listU", listU);
        model.addAttribute("accNamePage", "Management Account");
        model.addAttribute("addAccount", new UserDTO());
        model.addAttribute("listRole", userService.getListRole());
        return "adminDashboard/account";
    }

    @PostMapping("/admin/account/add")
    public String addUser(@ModelAttribute("addAccount") UserDTO userDTO, RedirectAttributes redirectAttributes) {

        try {
            userService.createUser(userService.userDTOtoUser(userDTO));
            redirectAttributes.addFlashAttribute("successMessage", "User added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding user: " + e.getMessage());
        }
        return "redirect:/admin/account";
    }

    @GetMapping("admin/account/viewInfo/{userId}")
    public String getDetailAccount(Model model, @PathVariable("userId") long id) {
        Optional<UserDetailDTO> userDetailDTO = userService.getInfoUser(id);
        if (userDetailDTO.isPresent()) {
            model.addAttribute("userDetail", userDetailDTO.get());
            return "adminDashboard/accountDetail";

        } else {
            return "redirect:/admin/account";
        }
    }

    @GetMapping("admin/account/delete/{userId}")
    public String deleteAccount(@PathVariable("userId") long id) {
         userService.deleteAcc(id);
        return "redirect:/admin/account";
    }


}
