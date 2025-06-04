package com.OLearning.controller.login;

import com.OLearning.dto.login.RegisterDTO;
import com.OLearning.entity.User;
import com.OLearning.service.user.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {


    private final UserServiceImpl userServiceImpl;

    public LoginController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "expired", required = false) String expired,
                            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "Đăng xuất thành công!");
        }
        if (expired != null) {
            model.addAttribute("warningMessage", "Phiên đăng nhập đã hết hạn!");
        }
        return "loginPage/login";
    }

    @GetMapping("/register")
    public String signUpPage(Model model) {
        model.addAttribute("user", new RegisterDTO());
        return "loginPage/signup";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") RegisterDTO registerDTO,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        //validation errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", registerDTO);
            return "loginPage/signup";
        }

        try {
            User savedUser = userServiceImpl.registerAccount(registerDTO);

            return "redirect:/select-role?userId=" + savedUser.getUserId();

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();

            if (errorMessage.contains("Email address is already registered")) {
                bindingResult.rejectValue("email", "email.duplicate", "Email address is already registered");
            } else if (errorMessage.contains("Passwords do not match")) {
                bindingResult.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match");
            } else if (errorMessage.contains("agree to the Terms")) {
                bindingResult.rejectValue("agreeToTerms", "terms.required", "You must agree to the Terms of Service and Privacy Policy");
            } else {
                model.addAttribute("error", errorMessage);
            }

            return "loginPage/signup";

        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            return "loginPage/signup";
        }
    }


    @GetMapping("/select-role")
    public String selectRolePage(@RequestParam("userId") Long userId, Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("roles", userServiceImpl.getListRole());
        return "loginPage/selectRole";
    }

    @PostMapping("/assign-role")
    public String assignRole(@RequestParam("role") String role,
                             @RequestParam("userId") Long userId,
                             RedirectAttributes redirectAttributes) {
        try {
            userServiceImpl.assignRoleToUser(userId, role);
            redirectAttributes.addFlashAttribute("success",
                    "Registration successful! Welcome to OLearning, " + userServiceImpl.getInfoUser(userId).get().getFullName() + "!");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Can not selected role.");
            return "redirect:/select-role?userId=" + userId;
        }
    }

    @GetMapping("/403")
    public String errorPage() {
        return "loginPage/403";
    }
}
