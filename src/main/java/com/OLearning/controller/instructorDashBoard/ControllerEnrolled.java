package com.OLearning.controller.instructorDashBoard;

import com.OLearning.dto.enrollment.EnrollmentDTO;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.enrollment.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/instructor/enrolled")
public class ControllerEnrolled {
    @Autowired
    private EnrollmentService enrollmentService;

    // Only keep the paging endpoint
    @GetMapping()
    public String getEnrolledPage(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "7") int size,
        Model model, ModelMap modelMap,
        @RequestHeader(value = "X-Requested-With", required = false) String requestedWith
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        Page<EnrollmentDTO> enrollment = enrollmentService.getEnrollmentsByInstructorId(userId, page, size);
        modelMap.put("courses", enrollment.getContent());
        modelMap.put("currentPage", page);
        modelMap.put("totalPages", enrollment.getTotalPages());
        modelMap.put("totalElements", enrollment.getTotalElements());
        modelMap.put("size", size);

        if ("XMLHttpRequest".equals(requestedWith)) {
            return "instructorDashboard/fragments/enrolledContent :: enrollment";
        }
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/enrolledContent :: enrollment");
        return "instructorDashboard/indexUpdate";
    }
}
