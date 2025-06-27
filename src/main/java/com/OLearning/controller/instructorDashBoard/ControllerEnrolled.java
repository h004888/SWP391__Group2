package com.OLearning.controller.instructorDashboard;

import com.OLearning.dto.enrollment.EnrollmentDTO;
import com.OLearning.entity.Enrollment;
import com.OLearning.entity.User;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.email.EmailService;
import com.OLearning.service.enrollment.EnrollmentService;
import com.OLearning.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/instructordashboard/enrolled")
public class ControllerEnrolled {
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    //in ra list enrolled
    @GetMapping()
    public String getEnrolledPage(@RequestParam(name = "page", defaultValue = "0") int page,
                                  @RequestParam(name = "size", defaultValue = "7") int size,
                                  Model model, ModelMap modelMap) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        Page<EnrollmentDTO> enrollment = enrollmentService.getEnrollmentsByInstructorId(userId, page, size);
        modelMap.put("courses", enrollment.getContent());
        modelMap.put("currentPage", page);
        modelMap.put("totalPages", enrollment.getTotalPages());
        modelMap.put("totalElements", enrollment.getTotalElements());
        modelMap.put("size", size);
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/enrolledContent :: enrollment");
        return "instructorDashboard/indexUpdate";
    }

    @PostMapping("/block/{enrollmentId}")
    public String blockEnrollment(@PathVariable int enrollmentId,
                                  RedirectAttributes redirectAttributes) {
        try {
            boolean success = enrollmentService.blockEnrollment(enrollmentId);

            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Enrollment blocked successfully");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Enrollment not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }

        return "redirect:/instructordashboard/enrolled";
    }

    @PostMapping("/unblock/{enrollmentId}")
    public String unblockEnrollment(@PathVariable int enrollmentId,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Gọi service để cập nhật trạng thái enrollment thành "active"
            boolean success = enrollmentService.unblockEnrollment(enrollmentId);

            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Enrollment unblocked successfully");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Enrollment not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }

        return "redirect:/instructordashboard/enrolled";
    }

    @PostMapping("/send-message")
    public String sendMessage(@RequestParam("recipientEmail") String recipientEmail,
                             @RequestParam("subject") String subject,
                             @RequestParam("content") String content,
                             @RequestParam(value = "enrollmentId", required = false, defaultValue = "0") int enrollmentId,
                             RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra enrollmentId
            if (enrollmentId <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid enrollment ID");
                return "redirect:/instructordashboard/enrolled";
            }
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User instructor = userService.findById(userDetails.getUserId());
            
            Enrollment enrollment = enrollmentService.getEnrollmentById(enrollmentId);
            
            if (enrollment == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Enrollment not found");
                return "redirect:/instructordashboard/enrolled";
            }
            
            // Gửi email
            emailService.sendInstructorMessageEmail(instructor, enrollment.getUser(), enrollment.getCourse(), subject, content);
            
            redirectAttributes.addFlashAttribute("successMessage", "Message sent successfully to " + enrollment.getUser().getFullName());
            return "redirect:/instructordashboard/enrolled";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to send message: " + e.getMessage());
            return "redirect:/instructordashboard/enrolled";
        }
    }
    @PostMapping("/details/{id}")
    public ResponseEntity<EnrollmentDTO> getRequestDetails(@PathVariable int id) {
        try {
            EnrollmentDTO request = enrollmentService.getRequestById(id);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
