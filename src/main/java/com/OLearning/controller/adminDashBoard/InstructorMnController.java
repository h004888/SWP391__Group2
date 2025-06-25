package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.user.UserDTO;
import com.OLearning.entity.InstructorRequest;
import com.OLearning.entity.User;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.InstructorRequest.InstructorRequestService;
import com.OLearning.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/mnInstructors")
public class InstructorMnController {

    private final Long roleInstructor = 3L;

    @Autowired
    private UserService userService;

    @Autowired
    private InstructorRequestService instructorRequestService;

    @GetMapping()
    public String getInstructorPage(
            Model model,
            @RequestParam(required = false, defaultValue = "0") int page) {

        Pageable prs = PageRequest.of(page, 5);
        Page<UserDTO> listInstructor = userService.getUsersByRoleWithPagination(roleInstructor, prs);
        List<UserDTO> listUser = listInstructor.getContent();

        Map<Long, Integer> userStudentCountMap = new HashMap<>();

        for (UserDTO user : listUser) {
//            int totalStudents = user.getCourse().stream();
//                    .mapToInt(course -> course.getTotalStudentEnrolled() != null ? course.getTotalStudentEnrolled() : 0)
//                    .sum();

//            userStudentCountMap.put(user.getUserId(), totalStudents);
        }

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", listInstructor.getTotalPages());
        model.addAttribute("totalItems", listInstructor.getTotalElements());

        model.addAttribute("fragmentContent", "adminDashBoard/fragments/instructorListContent :: instructorListContent");
        model.addAttribute("listInstructor", listUser);
        model.addAttribute("userStudentCountMap", userStudentCountMap);
        model.addAttribute("accNamePage", "Management Instructor");
        return "adminDashBoard/index";
    }

    @GetMapping("/request")
    public String getRequestInstrutor(@RequestParam(required = false, defaultValue = "0") int page,
                                      Model model) {
        Pageable prs = PageRequest.of(page, 5);
        Page<InstructorRequest> listRequest = instructorRequestService.getAllInstructorRequests(prs);
        List<InstructorRequest> listRequestContent = listRequest.getContent();

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", listRequest.getTotalPages());
        model.addAttribute("totalItems", listRequest.getTotalElements());

        model.addAttribute("listRequest", listRequestContent);
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/instructorRequestContent :: instructorRequestContent");
        return "adminDashBoard/index";
    }

    @GetMapping("/request/details/{id}")
    public ResponseEntity<InstructorRequest> getRequestDetails(@PathVariable Long id) {
        InstructorRequest request = instructorRequestService.getRequestById(id);
        return ResponseEntity.ok(request);
    }

    @PostMapping("/request/accept/{id}")
    public String acceptRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Get current logged-in admin
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                //config username is email to login in CustomerDetail
                String userName = userDetails.getUsername();
                UserDTO admin = userService.getUserByEmail(userName);
                instructorRequestService.acceptRequest(id, admin.getUserId());
                redirectAttributes.addFlashAttribute("successMessage", "Request accepted successfully");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error accepting request: " + e.getMessage());
        }
        return "redirect:/admin/mnInstructors/request";
    }

    @PostMapping("/request/reject/{id}")
    public String rejectRequest(@PathVariable Long id, 
                              @RequestParam String rejectionReason,
                              RedirectAttributes redirectAttributes) {
        try {
            // Get current logged-in admin
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                //config username is email to login in CustomerDetail
                String userName = userDetails.getUsername();
                UserDTO admin = userService.getUserByEmail(userName);
                instructorRequestService.rejectRequest(id, admin.getUserId(), rejectionReason);
                redirectAttributes.addFlashAttribute("successMessage", "Request rejected successfully");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting request: " + e.getMessage());
        }
        return "redirect:/admin/mnInstructors/request";
    }

    @GetMapping("/request/filter")
    public String filterRequests(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<InstructorRequest> listRequest = instructorRequestService.filterRequests(keyword, status, pageable);
        
        model.addAttribute("listRequest", listRequest.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", listRequest.getTotalPages());
        model.addAttribute("totalItems", listRequest.getTotalElements());
        
        return "adminDashBoard/fragments/instructorRequestContent :: requestTableFragment";
    }
}
