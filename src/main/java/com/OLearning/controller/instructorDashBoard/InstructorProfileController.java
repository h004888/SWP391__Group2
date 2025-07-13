package com.OLearning.controller.instructorDashBoard;

import com.OLearning.dto.user.UserDetailDTO;
import com.OLearning.dto.user.UserProfileUpdateDTO;
import com.OLearning.entity.User;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.repository.OrderDetailRepository;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.cloudinary.UploadFile;
import com.OLearning.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/instructor/profile")
public class InstructorProfileController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UploadFile uploadFile;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    // View Profile Page
    @GetMapping()
    public String viewProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        Optional<UserDetailDTO> userDetail = userService.getInfoUser(userId);
        if (userDetail.isPresent()) {
            model.addAttribute("userDetail", userDetail.get());
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/profileContent :: profileViewContent");
        } else {
            model.addAttribute("errorMessage", "User not found");
        }
        model.addAttribute("totalCourse", courseRepository.findByInstructorUserId(userId).size());
        model.addAttribute("totalEnrollment", enrollmentRepository.calculateSumEnrollment(userId).size());
        model.addAttribute("totalRevue", orderDetailRepository.sumRevenue(userId));
        return "instructorDashboard/indexUpdate";
    }

    // Edit Profile Page
    @GetMapping("/edit")
    public String editProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        Optional<UserDetailDTO> userDetail = userService.getInfoUser(userId);
        if (userDetail.isPresent()) {
            UserDetailDTO user = userDetail.get();
            
            // Create UserProfileUpdateDTO from UserDetailDTO
            UserProfileUpdateDTO profileUpdateDTO = new UserProfileUpdateDTO();
            profileUpdateDTO.setUserId(user.getUserId());
            profileUpdateDTO.setUsername(user.getUsername());
            profileUpdateDTO.setFullName(user.getFullName());
            profileUpdateDTO.setEmail(user.getEmail());
            profileUpdateDTO.setPhone(user.getPhone());
            profileUpdateDTO.setBirthDay(user.getBirthDay());
            profileUpdateDTO.setAddress(user.getAddress());
            profileUpdateDTO.setProfilePicture(user.getProfilePicture());
            profileUpdateDTO.setIsGooglePicture(user.getIsGooglePicture());
            profileUpdateDTO.setPersonalSkill(user.getPersonalSkill());
            
            model.addAttribute("userDetail", user);
            model.addAttribute("profileUpdateDTO", profileUpdateDTO);
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/profileEditContent :: profileEditContent");
        } else {
            model.addAttribute("errorMessage", "User not found");
        }

        return "instructorDashboard/indexUpdate";
    }

    // Update Profile
    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("profileUpdateDTO") UserProfileUpdateDTO profileUpdateDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();

            // Security check: ensure user can only update their own profile
            if (!userId.equals(profileUpdateDTO.getUserId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "You can only update your own profile");
                return "redirect:/instructor/profile/edit";
            }

            // Check for validation errors
            if (result.hasErrors()) {
                // Get user detail for form
                Optional<UserDetailDTO> userDetail = userService.getInfoUser(userId);
                if (userDetail.isPresent()) {
                    model.addAttribute("userDetail", userDetail.get());
                    model.addAttribute("fragmentContent", "instructorDashboard/fragments/profileEditContent :: profileEditContent");
                }
                return "instructorDashboard/indexUpdate";
            }

            User updatedUser = userService.updateProfile(userId, profileUpdateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            
            return "redirect:/instructor/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
            return "redirect:/instructor/profile/edit";
        }
    }

    // Upload Profile Picture
    @PostMapping("/upload-picture")
    @ResponseBody
    public String uploadProfilePicture(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        // Set content type
        response.setContentType("text/plain;charset=UTF-8");
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();

            System.out.println("Upload request received for user: " + userId);
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            System.out.println("Content type: " + file.getContentType());

            // Validate file
            if (file.isEmpty()) {
                System.out.println("Error: File is empty");
                return "error: Please select a file to upload";
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                System.out.println("Error: Invalid file type: " + contentType);
                return "error: Only image files are allowed";
            }

            // Validate file size (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                System.out.println("Error: File too large: " + file.getSize());
                return "error: File size must be less than 5MB";
            }

            System.out.println("Uploading to Cloudinary...");
            // Upload to Cloudinary
            String imageUrl = uploadFile.uploadImageFile(file);
            System.out.println("Cloudinary upload successful: " + imageUrl);
            
            System.out.println("Updating user profile picture...");
            // Update user profile picture
            userService.updateProfilePicture(userId, imageUrl);
            System.out.println("User profile picture updated successfully");
            
            return imageUrl;
        } catch (Exception e) {
            System.err.println("Error in uploadProfilePicture: " + e.getMessage());
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }

} 