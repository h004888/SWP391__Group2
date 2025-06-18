package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.user.UserDTO;
import com.OLearning.dto.user.UserDetailDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.InstructorRequest;
import com.OLearning.entity.User;
import com.OLearning.repository.UserRepository;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.courseReview.CourseReviewService;
import com.OLearning.service.email.EmailService;
import com.OLearning.service.enrollment.EnrollmentService;
import com.OLearning.service.instructorRequest.InstructorRequestService;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/mnInstructors")
public class InstructorMnController {

    private final Long roleInstructor = 2L;

    @Autowired
    private UserService userService;
    @Autowired
    private InstructorRequestService instructorRequestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private CourseReviewService courseReviewService;

    @GetMapping()
    public String getInstructorPage(
            Model model,
            @RequestParam(required = false, defaultValue = "0") int page) {

        Pageable prs = PageRequest.of(page, 6);
        Page<UserDTO> listInstructor = userService.getInstructorsByRoleIdOrderByCourseCountDesc(roleInstructor, prs);
        List<UserDTO> listUser = listInstructor.getContent();

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", listInstructor.getTotalPages());
        model.addAttribute("totalItems", listInstructor.getTotalElements());

        model.addAttribute("fragmentContent", "adminDashBoard/fragments/instructorListContent :: instructorListContent");
        model.addAttribute("listInstructor", listUser);
        model.addAttribute("enrollmentService", enrollmentService);
        model.addAttribute("accNamePage", "Management Instructor");
        return "adminDashBoard/index";
    }

    @GetMapping("/viewInfo/{userId}")
    public String getDetailAccount(
            Model model,
            @PathVariable("userId") long id,
            @RequestParam(defaultValue = "0") int coursePage,
            @RequestParam(defaultValue = "0") int reviewPage,
            @RequestParam(defaultValue = "5") int courseSize,
            @RequestParam(defaultValue = "5") int reviewSize) {
        Optional<UserDetailDTO> userDetailDTO = userService.getInfoUser(id);
        Page<CourseDTO> listCourses = courseService.findCourseByUserId(id, coursePage, courseSize);
        Page<CourseReview> listReview = courseReviewService.getCourseReviewsByInstructorId(id, reviewPage, reviewSize);

        model.addAttribute("listReview", listReview.getContent());
        model.addAttribute("reviewCurrentPage", reviewPage);
        model.addAttribute("reviewTotalPages", listReview.getTotalPages());
        model.addAttribute("reviewTotalItems", listReview.getTotalElements());

        model.addAttribute("listCourses", listCourses.getContent());
        model.addAttribute("courseCurrentPage", coursePage);
        model.addAttribute("courseTotalPages", listCourses.getTotalPages());
        model.addAttribute("courseTotalItems", listCourses.getTotalElements());

        model.addAttribute("userId", id);
        if (userDetailDTO.isPresent()) {
            model.addAttribute("fragmentContent", "adminDashBoard/fragments/instructorDetailContent :: instructorDetailContent");
            model.addAttribute("accNamePage", "Detail Account");
            model.addAttribute("userDetail", userDetailDTO.get());
            return "adminDashBoard/index";
        } else {
            return "redirect:/admin/account";
        }
    }

    @GetMapping("/viewInfo/{userId}/pagingCourse")
    public String pagingCourseInstructor(
            @PathVariable("userId") long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        Page<CourseDTO> listCourses = courseService.findCourseByUserId(id, page, size);
        model.addAttribute("listCourses", listCourses.getContent());
        model.addAttribute("courseCurrentPage", page);
        model.addAttribute("courseTotalPages", listCourses.getTotalPages());
        model.addAttribute("courseTotalItems", listCourses.getTotalElements());
        return "adminDashBoard/fragments/instructorDetailContent :: courseListFragment";
    }

    @GetMapping("/viewInfo/{userId}/pagingReview")
    public String pagingReview(
            @PathVariable("userId") long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {
        Page<CourseReview> listReview = courseReviewService.getCourseReviewsByInstructorId(id, page, size);
        model.addAttribute("listReview", listReview.getContent());
        model.addAttribute("reviewCurrentPage", page);
        model.addAttribute("reviewTotalPages", listReview.getTotalPages());
        model.addAttribute("reviewTotalItems", listReview.getTotalElements());
        return "adminDashBoard/fragments/instructorDetailContent :: reviewTableFragment";
    }

    @GetMapping("/filter")
    public String filterInstructors(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "grid") String view,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> listInstructor = userService.filterInstructors(keyword, pageable);
        List<UserDTO> listUser = listInstructor.getContent();

        model.addAttribute("listInstructor", listUser);
        model.addAttribute("enrollmentService", enrollmentService);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", listInstructor.getTotalPages());
        model.addAttribute("totalItems", listInstructor.getTotalElements());

        return view.equals("grid") ?
                "adminDashBoard/fragments/instructorListContent :: instructorTableFragment" :
                "adminDashBoard/fragments/instructorListContent :: instructorListTableFragment";
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
    
    @PostMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(
            @RequestParam Long instructorId,
            @RequestParam String message) {
        try {
            User instructor = userRepository.findById(instructorId)
                    .orElseThrow(() -> new RuntimeException("Instructor not found"));
            emailService.sendMessageToInstructor(
                    instructor.getEmail(),
                    instructor.getFullName(),
                    message
            );
            return ResponseEntity.ok().body("Message sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error sending message: " + e.getMessage());
        }
    }

}
