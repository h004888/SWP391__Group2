package com.OLearning.controller.adminDashBoard;

import com.OLearning.service.termsAndCondition.TermsAndConditionService;
import com.OLearning.entity.TermsAndCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.UUID;

@Controller
@RequestMapping("/admin/businessRule")
public class BusinessRuleController {
    @Autowired
    private TermsAndConditionService termsAndConditionService;

    @RequestMapping
    public String getBusinessRulePage(Model model) {
        model.addAttribute("terms", termsAndConditionService.getAll());
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/businessRuleContent :: businessRuleContent");
        return "adminDashBoard/index";
    }

    @PostMapping("/add")
    public String addClause(
        @RequestParam String sectionTitle,
        @RequestParam String content,
        @RequestParam(required = false, defaultValue = "ALL") String roleTarget,
        RedirectAttributes redirectAttributes
    ) {
        TermsAndCondition clause = TermsAndCondition.builder()
            .sectionTitle(sectionTitle)
            .content(content)
            .roleTarget(roleTarget)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        termsAndConditionService.save(clause);
        redirectAttributes.addFlashAttribute("successMessage", "Clause added successfully!");
        return "redirect:/admin/businessRule";
    }

    @PostMapping("/edit")
    public String editClause(
        @RequestParam Long id,
        @RequestParam String sectionTitle,
        @RequestParam String content,
        @RequestParam String roleTarget,
        RedirectAttributes redirectAttributes
    ) {
        TermsAndCondition clause = termsAndConditionService.getById(id);
        clause.setSectionTitle(sectionTitle);
        clause.setContent(content);
        clause.setRoleTarget(roleTarget);
        clause.setUpdatedAt(LocalDateTime.now());
        termsAndConditionService.save(clause);
        redirectAttributes.addFlashAttribute("successMessage", "Clause updated successfully!");
        return "redirect:/admin/businessRule";
    }

    @PostMapping("/delete")
    public String deleteClause(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        termsAndConditionService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Clause deleted successfully!");
        return "redirect:/admin/businessRule";
    }

    @RequestMapping("/exportPdf")
    public ResponseEntity<byte[]> exportPdf() {
        byte[] pdfBytes = termsAndConditionService.exportAllToPdf();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        headers.setContentDispositionFormData("attachment", "business_rules_" + randomSuffix + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
