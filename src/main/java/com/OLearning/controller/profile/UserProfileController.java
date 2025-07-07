package com.OLearning.controller.profile;

import com.OLearning.dto.user.UserProfileEditDTO;
import com.OLearning.service.cloudinary.UploadFile;
import com.OLearning.service.user.UserService;
import com.OLearning.service.notification.NotificationService;
import com.OLearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class UserProfileController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private UploadFile uploadFile;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping()
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<UserProfileEditDTO> profileOpt = userService.getProfileByUsername(userDetails.getUsername());
        model.addAttribute("profile", profileOpt.orElse(new UserProfileEditDTO()));

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isInstructor = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));
        
        // Add unread notification count for students
        if (!isAdmin && !isInstructor) {
            userRepository.findByUsername(userDetails.getUsername()).ifPresent(user -> {
                long unreadCount = notificationService.countUnreadByUserId(user.getUserId());
                model.addAttribute("unreadCount", unreadCount);
            });
        }
        
        if (isAdmin) {
            model.addAttribute("fragmentContent", "adminDashBoard/fragments/profileContent :: profileContent");
            return "adminDashBoard/index";
        } else if (isInstructor) {
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/profileContent :: profileContent");
            return "instructorDashboard/index";
        } else {
            return "homePage/profile";
        }
    }

    @GetMapping("/edit")
    public String showEditProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<UserProfileEditDTO> profileOpt = userService.getProfileByUsername(userDetails.getUsername());
        model.addAttribute("profile", profileOpt.orElse(new UserProfileEditDTO()));

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isInstructor = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));
        boolean isStudent = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        
        // Add unread notification count for students
        if (isStudent) {
            userRepository.findByUsername(userDetails.getUsername()).ifPresent(user -> {
                long unreadCount = notificationService.countUnreadByUserId(user.getUserId());
                model.addAttribute("unreadCount", unreadCount);
            });
        }
        
        if (isAdmin) {
            model.addAttribute("fragmentContent", "adminDashBoard/fragments/editProfileContent :: editProfileContent");
            return "adminDashBoard/index";
        } else if (isInstructor) {
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/editProfileContent :: editProfileContent");
            return "instructorDashboard/indexUpdate";
        } else if (isStudent) {
            return "homePage/editProfile";
        } else {
            return "homePage/editProfile";
        }
    }

    @PostMapping("/edit")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute("profile") UserProfileEditDTO profileEditDTO,
                                @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                                Model model) {
        try {
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String imageUrl = uploadFile.uploadImageFile(avatarFile);
                profileEditDTO.setAvatarUrl(imageUrl);
            } else {
                // Nếu không chọn ảnh mới, giữ lại link cũ
                Optional<UserProfileEditDTO> oldProfile = userService.getProfileByUsername(userDetails.getUsername());
                oldProfile.ifPresent(old -> profileEditDTO.setAvatarUrl(old.getAvatarUrl()));
            }
            
            userService.updateProfileByUsername(userDetails.getUsername(), profileEditDTO);
            // Sau khi lưu thành công, redirect về trang profile
            return "redirect:/profile";
        } catch (IOException e) {
            model.addAttribute("error", "Không thể tải lên ảnh. Vui lòng thử lại.");
            return showEditProfile(userDetails, model);
        }
    }
}