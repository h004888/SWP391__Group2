package com.OLearning.controller.homePage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import com.OLearning.service.instructorRequest.InstructorRequestService;
import com.OLearning.entity.InstructorRequest;
import com.OLearning.dto.instructorRequest.InstructorRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.entity.User;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import com.OLearning.mapper.instructorRequest.InstructorRequestMapper;
import org.springframework.web.bind.annotation.RequestParam;
import com.OLearning.service.termsAndCondition.TermsAndConditionService;
import com.OLearning.entity.TermsAndCondition;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Collections;
import java.util.List;
import com.OLearning.service.cloudinary.UploadFile;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class BecomeInstructorController {

    @Autowired
    private InstructorRequestService instructorRequestService;

    @Autowired
    private TermsAndConditionService termsAndConditionService;

    @Autowired
    private UploadFile uploadFile;

    @GetMapping("/become-instructor")
    public String showBecomeInstructorPage(Model model, @RequestParam(value = "success", required = false) String success) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUserEntity();
        InstructorRequestDTO dto = new InstructorRequestDTO();
        dto.setName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setPersonalSkill(user.getPersonalSkill());
        model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
        model.addAttribute("fragmentContent", "homePage/fragments/becomeInstructorContent :: becomeInstructorContent");
        model.addAttribute("instructorRequestDTO", dto);
        InstructorRequest latestRequest = instructorRequestService.findLatestByUserId(user.getUserId());
        if (latestRequest != null) {
            String status = latestRequest.getStatus();
            model.addAttribute("requestStatus", status);
        }
        if (success != null) {
            model.addAttribute("successMessage", "Gửi yêu cầu thành công! Vui lòng chờ xét duyệt.");
        }
        return "homePage/index";
    }

    @GetMapping("/become-instructor/terms")
    @ResponseBody
    public List<TermsAndCondition> getInstructorTerms() {
        return termsAndConditionService.getByRoles(Collections.singletonList("INSTRUCTOR"));
    }

    @PostMapping("/become-instructor")
    public String sendBecomeInstructorRequest(@Valid @ModelAttribute("instructorRequestDTO") InstructorRequestDTO instructorRequestDTO,
                                                BindingResult bindingResult, 
                                                Model model, 
                                                @RequestParam(value = "agreeTerms", required = false) String agreeTerms) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUserEntity();

        boolean missingInfo = user.getFullName() == null || user.getFullName().isBlank()
            || user.getEmail() == null || user.getEmail().isBlank()
            || user.getPhone() == null || user.getPhone().isBlank();
        if (missingInfo) {
            model.addAttribute("showProfileModal", true);
            model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
            model.addAttribute("fragmentContent", "homePage/fragments/becomeInstructorContent :: becomeInstructorContent");
            model.addAttribute("instructorRequestDTO", instructorRequestDTO);
            return "homePage/index";
        }
 
        if (agreeTerms == null) {
            model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
            model.addAttribute("fragmentContent", "homePage/fragments/becomeInstructorContent :: becomeInstructorContent");
            model.addAttribute("instructorRequestDTO", instructorRequestDTO);
            model.addAttribute("termsError", "Bạn phải đồng ý với điều khoản & dịch vụ để tiếp tục.");
            return "homePage/index";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
            model.addAttribute("fragmentContent", "homePage/fragments/becomeInstructorContent :: becomeInstructorContent");
            return "homePage/index";
        }
        // Xử lý upload file
        MultipartFile file = instructorRequestDTO.getInstructorFile();
        if (file != null && !file.isEmpty()) {
            String filename = file.getOriginalFilename();
            long maxSize = 5 * 1024 * 1024; // 5MB
            String[] allowedExts = {".docx", ".jpg", ".jpeg", ".png"};
            boolean validExt = false;
            if (filename != null) {
                String lower = filename.toLowerCase();
                for (String ext : allowedExts) {
                    if (lower.endsWith(ext)) {
                        validExt = true;
                        break;
                    }
                }
            }
            if (!validExt) {
                model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
                model.addAttribute("fragmentContent", "homePage/fragments/becomeInstructorContent :: becomeInstructorContent");
                model.addAttribute("instructorRequestDTO", instructorRequestDTO);
                model.addAttribute("termsError", "Chỉ cho phép file ảnh (.jpg, .jpeg, .png) hoặc .docx!");
                return "homePage/index";
            }
            if (file.getSize() > maxSize) {
                model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
                model.addAttribute("fragmentContent", "homePage/fragments/becomeInstructorContent :: becomeInstructorContent");
                model.addAttribute("instructorRequestDTO", instructorRequestDTO);
                model.addAttribute("termsError", "File quá lớn (tối đa 5MB). Vui lòng chọn file nhỏ hơn!");
                return "homePage/index";
            }
            if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
                model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
                model.addAttribute("fragmentContent", "homePage/fragments/becomeInstructorContent :: becomeInstructorContent");
                model.addAttribute("instructorRequestDTO", instructorRequestDTO);
                model.addAttribute("termsError", "Không hỗ trợ gửi file PDF. Vui lòng chọn file khác!");
                return "homePage/index";
            }
            try {
                String url = uploadFile.uploadFile(file, "raw");
                instructorRequestDTO.setFileUrl(url);
            } catch (Exception e) {
                model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");
                model.addAttribute("fragmentContent", "homePage/fragments/becomeInstructorContent :: becomeInstructorContent");
                model.addAttribute("instructorRequestDTO", instructorRequestDTO);
                model.addAttribute("termsError", "Lỗi upload file: " + e.getMessage());
                return "homePage/index";
            }
        }
        InstructorRequest request = InstructorRequestMapper.mapToEntity(instructorRequestDTO, user);
        instructorRequestService.save(request);
        return "redirect:/become-instructor?success";
    }
}
