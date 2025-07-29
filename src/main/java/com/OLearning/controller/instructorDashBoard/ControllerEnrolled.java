package com.OLearning.controller.instructorDashBoard;

import com.OLearning.dto.enrollment.EnrollMaxMinDTO;
import com.OLearning.dto.enrollment.EnrollmentDTO;
import com.OLearning.dto.enrollment.CourseEnrollmentStatsDTO;
import com.OLearning.dto.enrollment.EnrollmentFilterDTO;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.enrollment.EnrollmentService;
import com.OLearning.service.enrollment.EnrollmentStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.OLearning.entity.Enrollment;
import com.OLearning.entity.User;
import com.OLearning.entity.Course;
import java.util.List;

@Controller
@RequestMapping("/instructor/enrolled")
public class ControllerEnrolled {
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private EnrollmentStatisticsService enrollmentStatisticsService;

    @GetMapping("/search")
    public String searchEnrollments(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "5") int size,
        @RequestParam(name = "courseId", required = false) Long courseId,
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "search", required = false) String searchTerm,
        Model model
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        // Create filter DTO
        EnrollmentFilterDTO filterDTO = new EnrollmentFilterDTO();
        filterDTO.setPage(page);
        filterDTO.setSize(size);
        filterDTO.setCourseId(courseId);
        filterDTO.setStatus(status);
        filterDTO.setSearchTerm(searchTerm);

        // Get filtered enrollments
        Page<EnrollmentDTO> enrollment = enrollmentService.findEnrollmentsWithFilters(userId, filterDTO);

        // Add to model
        model.addAttribute("courses", enrollment.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", enrollment.getTotalPages());
        model.addAttribute("totalElements", enrollment.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("selectedCourseId", courseId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("searchTerm", searchTerm);

        return "instructorDashboard/fragments/enrolledContent :: tableContent";
    }

    @GetMapping()
    public String getEnrolledPage(
        Model model, ModelMap modelMap
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        // Get initial statistics
        EnrollMaxMinDTO max = enrollmentService.enrollmentMax(userId);
        EnrollMaxMinDTO min = enrollmentService.enrollmentMin(userId);
        Integer maxCompleted = enrollmentService.completeMax(userId);
        Integer minCompleted = enrollmentService.completeMin(userId);
        Integer maxOnGoing = enrollmentService.onGoingMax(userId);
        Integer minOnGoing = enrollmentService.onGoingMin(userId);

        // Get instructor's courses for filter dropdown
        List<Course> instructorCourses = enrollmentService.getInstructorCourses(userId);

        // Add statistics to model
        modelMap.put("maxCompleted", maxCompleted);
        modelMap.put("minCompleted", minCompleted);
        modelMap.put("maxOnGoing", maxOnGoing);
        modelMap.put("minOnGoing", minOnGoing);
        modelMap.put("max", max);
        modelMap.put("min", min);
        modelMap.put("instructorCourses", instructorCourses);
        modelMap.put("sumEnroll", enrollmentService.countTotalEnrollment(userId));
        modelMap.put("sumEnrollOnGoing", enrollmentService.countTotalEnrollmentOnGoing(userId));
        modelMap.put("sumEnrollCompleted", enrollmentService.countTotalEnrollmentCompelted(userId));

        model.addAttribute("fragmentContent", "instructorDashboard/fragments/enrolledContent :: enrollmentPage");
        return "instructorDashboard/indexUpdate";
    }

    @GetMapping("/detail/{id}")
    public String getEnrollmentDetail(@PathVariable("id") int id, Model model) {
        Enrollment enrollment = enrollmentService.getEnrollmentById(id);
        if (enrollment == null) {
            model.addAttribute("errorMessage", "Enrollment not found");
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/enrollmentDetailModalContent :: errorContent");
            return "instructorDashboard/indexUpdate";
        }
        User user = enrollment.getUser();
        model.addAttribute("user", user);
        model.addAttribute("enrollment", enrollment);
        model.addAttribute("fragmentContent", "instructorDashboard/fragments/enrollmentDetailModalContent :: modalContent");
        return "instructorDashboard/indexUpdate";
    }
}
