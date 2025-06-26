package com.OLearning.controller.adminDashBoard;

import com.OLearning.entity.Report;
import com.OLearning.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/reports")
public class ReportController {
    @Autowired
    private ReportRepository reportRepository;

    @GetMapping
    public String listReports(@RequestParam(required = false) String type,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) String keyword,
                             Model model) {
        // Đơn giản: lấy tất cả, có thể mở rộng filter sau
        List<Report> reports = reportRepository.findAll();
        model.addAttribute("reports", reports);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/reportContent :: reportContent");
        return "adminDashBoard/index";
    }

    @GetMapping("/view/{id}")
    public String viewReport(@PathVariable Long id, Model model) {
        Report report = reportRepository.findById(id).orElse(null);
        model.addAttribute("report", report);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/reportDetailContent :: reportDetailContent");
        model.addAttribute("accNamePage", "Chi tiết báo cáo");
        return "adminDashBoard/index";
    }

    @PostMapping("/process/{id}")
    public String processReport(@PathVariable Long id) {
        Report report = reportRepository.findById(id).orElse(null);
        if (report != null && !"processed".equalsIgnoreCase(report.getStatus())) {
            report.setStatus("processed");
            reportRepository.save(report);
        }
        return "redirect:/admin/reports";
    }
}
