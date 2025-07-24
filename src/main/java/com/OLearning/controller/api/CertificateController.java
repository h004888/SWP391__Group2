package com.OLearning.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.OLearning.entity.Certificate;
import com.OLearning.service.certificate.CertificateService;

@Controller
@RequestMapping("/certificates")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;

    @GetMapping("/generate")
    public String generateCertificate(@RequestParam Long userId, @RequestParam Long courseId, Model model) {
        Certificate cert = certificateService.findByUser_UserIdAndCourse_CourseId(userId, courseId);
        model.addAttribute("cert", cert);
        System.out.println(cert);
        return "/userPage/certificate";
    }

    

}
