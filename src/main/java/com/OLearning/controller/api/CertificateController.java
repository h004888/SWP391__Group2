package com.OLearning.controller.api;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.OLearning.entity.Certificate;
import com.OLearning.entity.User;
import com.OLearning.service.certificate.CertificateService;
import com.OLearning.service.enrollment.EnrollmentService;
import com.OLearning.service.category.CategoryService;
import com.OLearning.service.user.UserService;
import com.OLearning.security.CustomUserDetails;

@Controller
@RequestMapping("/certificates")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping()
    private String getAllCertificate(Model model, Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size,
            @RequestParam(required = false) String search) {
        User user = extractCurrentUser(principal);
        if (user == null) {
            return "redirect:/login";
        }

        Page<Certificate> certificates = certificateService.findByUser_UserId(user.getUserId(),
                PageRequest.of(page, size));

        if (certificates.isEmpty()) {
            model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
            return "homePage/error-404";
        }
        model.addAttribute("certificates", certificates);
        model.addAttribute("activeMenu", "certificate");
        model.addAttribute("user", user);
        model.addAttribute("categories", categoryService.getListCategories().stream().limit(5).toList());

        return "/userPage/userCertificate";
    }

    private User extractCurrentUser(Principal principal) {
        if (principal instanceof Authentication authentication) {
            Object principalObj = authentication.getPrincipal();
            if (principalObj instanceof CustomUserDetails customUserDetails) {
                return customUserDetails.getUserEntity();
            }
        }
        return null;
    }

    @GetMapping("/generate")
    public String generateCertificate(@RequestParam Long userId, @RequestParam Long courseId, Model model) {
        Certificate cert = certificateService.findByUser_UserIdAndCourse_CourseId(userId, courseId);
        if (cert == null) {
            model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
            return "homePage/error-404";
        }
        System.out.println("certificate: " + cert);
        System.out.println("UserName: " + cert.getUser().getFullName());
        model.addAttribute("cert", cert);
        System.out.println(cert);
        return "/userPage/certificate";
    }

}
