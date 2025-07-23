package com.OLearning.controller.login;

import com.OLearning.dto.login.RegisterDTO;
import com.OLearning.entity.User;
import com.OLearning.service.email.PasswordResetService;
import com.OLearning.service.user.UserService;
import com.OLearning.service.user.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class LoginController {


    private final UserService userService;
    private final PasswordResetService passwordResetService;

    public LoginController(UserService userService, PasswordResetService passwordResetService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "expired", required = false) String expired,
                            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Incorrect username or password!");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "Logout successful!");
        }
        if (expired != null) {
            model.addAttribute("warningMessage", "Session has expired!");
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
                model.addAttribute("errorMessage", "Admin accounts are not allowed to log in here!");
            } else {
                model.addAttribute("errorMessage", "Incorrect username or password!");
            }
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Logout successful!");
        }
        if (expired != null) {
            model.addAttribute("warningMessage", "Session has expired!");
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
            userService.registerAccount(registerDTO);
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
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please verify your email with the OTP sent to your email address.");
        return "redirect:/otp-verification?email=" + registerDTO.getEmail() + "&type=verifyEmail";
    }

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
                                       @RequestParam(value = "type", required = false, defaultValue = "forgotPassword") String type,
                                       RedirectAttributes redirectAttributes) {
        try {
            // Check if user has pending OTP (optional rate limiting)
            if (passwordResetService.hasPendingOTP(email)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "You already have a pending OTP. Please check your email.");
                return "redirect:/otp-verification?email=" + email + "&type=" + type;
            }

            passwordResetService.generateOTP(email, type);
            redirectAttributes.addFlashAttribute("successMessage",
                    ("verifyEmail".equals(type) ? "A verification code has been sent to your email. Please check your inbox." : "An OTP has been sent to your email. Please check your inbox."));
            return "redirect:/otp-verification?email=" + email + "&type=" + type;

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/forgot-password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An error occurred. Please try again later.");
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/otp-verification")
    public String getOtpVerificationPage(@RequestParam("email") String email,
                                         @RequestParam(value = "error", required = false) String error,
                                         @RequestParam(value = "type", required = false, defaultValue = "forgotPassword") String type,
                                         Model model) {
        model.addAttribute("email", email);
        model.addAttribute("type", type);
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid or expired OTP code.");
        }
        return "loginPage/normalLogin/otpVerification";
    }

    @PostMapping("/otp-verification")
    public String verifyOtp(@RequestParam("email") String email,
                            @RequestParam("otp") String otp,
                            @RequestParam(value = "type", required = false, defaultValue = "forgotPassword") String type,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (passwordResetService.verifyOTP(email, otp)) {
            if ("verifyEmail".equals(type)) {
                // Cập nhật trạng thái xác thực email
                Optional<User> userOpt = userService.findByEmail(email);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    if (!Boolean.TRUE.equals(user.getEmailVerified())) {
                        user.setEmailVerified(true);
                        userService.save(user);
                    }
                }
                redirectAttributes.addFlashAttribute("successMessage", "Email verification successful! Please log in.");
                return "redirect:/login";
            } else {
            return "redirect:/reset-password?email=" + email + "&otp=" + otp;
            }
        } else {
            model.addAttribute("email", email);
            model.addAttribute("type", type);
            model.addAttribute("errorMessage", "Invalid or expired OTP code.");
            return "redirect:/otp-verification?email=" + email + "&type=" + type + "&error=true";
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
                    "Password confirmation does not match!");
            return "redirect:/reset-password?email=" + email + "&otp=" + otp;
        }

        try {
            String result = passwordResetService.resetPassword(email, otp, newPassword);

            if (result.equals("Đổi mật khẩu thành công!")) {
                redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", result);
                return "redirect:/reset-password?email=" + email + "&otp=" + otp;
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An error occurred while changing the password!");
            return "redirect:/reset-password?email=" + email + "&otp=" + otp;
        }
    }

    @GetMapping("/change-password-oauth2")
    public String getResetPasswordOauth2Page() {
        return "loginPage/normalLogin/changePassword-Oauth2";
    }

    @PostMapping("/change-password-oauth2")
    public String handleResetPasswordOauth2(@RequestParam String newPassword,
                                            @RequestParam String confirmPassword,
                                            java.security.Principal principal,
                                            RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password confirmation does not match!");
            return "redirect:/change-password-oauth2";
        }
        String email = principal.getName();
        userService.updatePasswordByEmail(email, newPassword);
        redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        return "redirect:/home";
    }
}
