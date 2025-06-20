package com.OLearning.controller.login;

import com.OLearning.dto.login.RegisterDTO;
import com.OLearning.service.email.PasswordResetService;
import com.OLearning.service.user.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {


    private final UserServiceImpl userServiceImpl;
    private final PasswordResetService passwordResetService;

    public LoginController(UserServiceImpl userServiceImpl, PasswordResetService passwordResetService) {
        this.userServiceImpl = userServiceImpl;
        this.passwordResetService = passwordResetService;
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
        return "loginPage/normalLogin/login";
    }

    @GetMapping("/dashboard_login")
    public String adminLoginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "expired", required = false) String expired,
                            Model model) {

        if (error != null) {
            if ("unauthorized_admin_login".equals(error)) {
                model.addAttribute("errorMessage", "Tài khoản admin không được phép đăng nhập tại đây!");
            } else {
                model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
            }
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Đăng xuất thành công!");
        }
        if (expired != null) {
            model.addAttribute("warningMessage", "Phiên đăng nhập đã hết hạn!");
        }
        return "loginPage/adminLogin/sign-in";
    }

    @GetMapping("/register")
    public String signUpPage(Model model) {
        model.addAttribute("user", new RegisterDTO());
        return "loginPage/normalLogin/signup";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") RegisterDTO registerDTO,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        //validation errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", registerDTO);
            return "loginPage/normalLogin/signup";
        }

        try {
           userServiceImpl.registerAccount(registerDTO);
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

            return "loginPage/normalLogin/signup";

        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            return "loginPage/normalLogin/signup";
        }
        return "redirect:/login";
    }


//    @GetMapping("/select-role")
//    public String selectRolePage(@RequestParam("userId") Long userId, Model model) {
//        model.addAttribute("userId", userId);
//        model.addAttribute("roles", userServiceImpl.getListRole());
//        return "loginPage/selectRole";
//    }
//
//    @PostMapping("/assign-role")
//    public String assignRole(@RequestParam("role") String role,
//                             @RequestParam("userId") Long userId,
//                             RedirectAttributes redirectAttributes) {
//        try {
//            userServiceImpl.assignRoleToUser(userId, role);
//            redirectAttributes.addFlashAttribute("success",
//                    "Registration successful! Welcome to OLearning, " + userServiceImpl.getInfoUser(userId).get().getFullName() + "!");
//            return "redirect:/login";
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "You need selected role before.");
//            return "redirect:/select-role?userId=" + userId;
//        }
//    }

    @GetMapping("/403")
    public String errorPage() {
        return "loginPage/403";
    }


    @GetMapping("/forgot-password")
    public String getForgotPasswordPage() {
        return "loginPage/normalLogin/forgotPassword";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String email,
                                       RedirectAttributes redirectAttributes) {
        try {
            // Check if user has pending OTP (optional rate limiting)
            if (passwordResetService.hasPendingOTP(email)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Bạn đã có OTP đang chờ xử lý. Vui lòng kiểm tra email.");
                return "redirect:/otp-verification?email=" + email;
            }

            passwordResetService.generateOTP(email);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã gửi mã OTP đến email của bạn. Vui lòng kiểm tra hộp thư.");
            return "redirect:/otp-verification?email=" + email;

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/forgot-password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Có lỗi xảy ra. Vui lòng thử lại sau.");
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/otp-verification")
    public String getOtpVerificationPage(@RequestParam("email") String email,
                                         Model model) {
        model.addAttribute("email", email);
        return "loginPage/normalLogin/otpVerification";
    }

    @PostMapping("/otp-verification")
    public String verifyOtp(@RequestParam("email") String email,
                            @RequestParam("otp") String otp,
                            Model model) {
        if (passwordResetService.verifyOTP(email, otp)) {
            return "redirect:/reset-password?email=" + email + "&otp=" + otp;
        } else {
            model.addAttribute("email", email);
            model.addAttribute("errorMessage", "Mã OTP không hợp lệ hoặc đã hết hạn.");
            return "loginPage/normalLoginotpVerification";
        }
    }


    @GetMapping("/reset-password")
    public String getResetPasswordPage(@RequestParam String email,
                                       @RequestParam String otp,
                                       Model model) {
        model.addAttribute("email", email);
        model.addAttribute("otp", otp);
        return "loginPage/normalLogin/resetPassword";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String email,
                                      @RequestParam String otp,
                                      @RequestParam String newPassword,
                                      @RequestParam String confirmPassword,
                                      RedirectAttributes redirectAttributes) {

        // Validate password confirmation
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Mật khẩu xác nhận không khớp!");
            return "redirect:/reset-password?email=" + email + "&otp=" + otp;
        }

        try {
            String result = passwordResetService.resetPassword(email, otp, newPassword);

            if (result.equals("Đổi mật khẩu thành công!")) {
                redirectAttributes.addFlashAttribute("successMessage", result);
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", result);
                return "redirect:/reset-password?email=" + email + "&otp=" + otp;
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Có lỗi xảy ra khi đổi mật khẩu!");
            return "redirect:/reset-password?email=" + email + "&otp=" + otp;
        }
    }
}
