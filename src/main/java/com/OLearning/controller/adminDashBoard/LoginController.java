//package com.OLearning.controller.adminDashBoard;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//@RequestMapping("/login")
//public class    LoginController {
//    @GetMapping()
//    public String loginPage(@RequestParam(value = "error", required = false) String error,
//                            @RequestParam(value = "logout", required = false) String logout,
//                            Model model) {
//
//        if (error != null) {
//            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
//        }
//
//        if (logout != null) {
//            model.addAttribute("successMessage", "Đăng xuất thành công!");
//        }
//
//        return "login/login";
//    }
//}
