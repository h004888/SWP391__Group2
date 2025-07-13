package com.OLearning.controller.adminDashBoard;

import com.OLearning.entity.Report;
import com.OLearning.repository.ReportRepository;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.service.comment.CommentService;
import com.OLearning.entity.CourseReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@Controller
@RequestMapping("/admin/reports")
public class ReportMnController {
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private CourseReviewRepository courseReviewRepository;
    @Autowired
    private CommentService commentService;

    @GetMapping
    public String listReports(@RequestParam(required = false) String type,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size,
                             Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Report> reportPage;
        if (type != null && !type.isEmpty() && status != null && !status.isEmpty()) {
            reportPage = reportRepository.findByReportTypeAndStatus(type, status, pageable);
        } else if (type != null && !type.isEmpty()) {
            reportPage = reportRepository.findByReportType(type, pageable);
        } else if (status != null && !status.isEmpty()) {
            reportPage = reportRepository.findByStatus(status, pageable);
        } else {
            reportPage = reportRepository.findAll(pageable);
        }
        model.addAttribute("reports", reportPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reportPage.getTotalPages());
        model.addAttribute("totalItems", reportPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("type", type);
        model.addAttribute("status", status);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/reportContent :: reportContent");
        return "adminDashBoard/index";
    }

    @GetMapping("/view/{id}")
    public String viewReport(@PathVariable Long id, Model model) {
        Report report = reportRepository.findById(id).orElse(null);
        model.addAttribute("report", report);
        // Nếu là report comment, truyền thêm comment bị report
        if (report != null && "COMMENT".equalsIgnoreCase(report.getReportType()) && report.getCommentId() != null) {
            CourseReview comment = courseReviewRepository.findById(report.getCommentId()).orElse(null);
            model.addAttribute("reportedComment", comment);
        }
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

    @PostMapping("/hide-comment")
    @ResponseBody
    public String hideComment(@RequestBody Map<String, Object> payload) {
        Long reviewId = Long.valueOf(payload.get("reviewId").toString());
        boolean hidden = Boolean.parseBoolean(payload.get("hidden").toString());
        commentService.setCommentHidden(reviewId, hidden);
        return "success";
    }
}
