package com.OLearning.controller.userDashboard;

import com.OLearning.dto.user.UserProfileEditDTO;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.cloudinary.UploadFile;
import com.OLearning.service.notification.NotificationService;
import com.OLearning.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.OLearning.security.CustomUserDetailsService;

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

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @GetMapping()
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<UserProfileEditDTO> profileOpt = userService.getProfileByUsername(userDetails.getUsername());
        model.addAttribute("profile", profileOpt.orElse(new UserProfileEditDTO()));

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isInstructor = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));

        if (!isAdmin && !isInstructor) {
            userRepository.findByUsername(userDetails.getUsername()).ifPresent(user -> {
                long unreadCount = notificationService.countUnreadByUserId(user.getUserId());
                model.addAttribute("unreadCount", unreadCount);
            });
        }
        model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");

        if (isAdmin) {
            model.addAttribute("fragmentContent", "adminDashBoard/fragments/profileContent :: profileContent");
            return "adminDashBoard/index";
        } else if (isInstructor) {
            model.addAttribute("fragmentContent", "instructorDashboard/fragments/profileContent :: profileContent");
            return "instructorDashboard/index";
        } else {
            model.addAttribute("fragmentContent", "homePage/fragments/profileFragment :: profile");
            return "homePage/index";
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

        if (isStudent) {
            userRepository.findByUsername(userDetails.getUsername()).ifPresent(user -> {
                long unreadCount = notificationService.countUnreadByUserId(user.getUserId());
                model.addAttribute("unreadCount", unreadCount);
            });
        }
        model.addAttribute("navCategory", "homePage/fragments/navHeader :: navHeaderDefault");

        if (isAdmin) {
            model.addAttribute("fragmentContent", "adminDashBoard/fragments/editProfileContent :: editProfileContent");
            return "adminDashBoard/index";
        } else if (isInstructor) {
            model.addAttribute("fragmentContent",
                    "instructorDashboard/fragments/editProfileContent :: editProfileContent");
            return "instructorDashboard/indexUpdate";
        } else if (isStudent) {
            model.addAttribute("fragmentContent", "homePage/fragments/editProfileFragment :: editProfile");
            return "homePage/index";
        } else {
            model.addAttribute("fragmentContent", "homePage/fragments/editProfileFragment :: editProfile");
            return "homePage/index";
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
                Optional<UserProfileEditDTO> oldProfile = userService.getProfileByUsername(userDetails.getUsername());
                oldProfile.ifPresent(old -> profileEditDTO.setAvatarUrl(old.getAvatarUrl()));
            }
            userService.updateProfileByUsername(userDetails.getUsername(), profileEditDTO);
            UserDetails updatedUserDetails = customUserDetailsService.loadUserByUsername(
                    profileEditDTO.getEmail() != null ? profileEditDTO.getEmail() : userDetails.getUsername());
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedUserDetails,
                    userDetails.getPassword(),
                    updatedUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            return "redirect:/profile";
        } catch (IOException e) {
            model.addAttribute("error", "Không thể tải lên ảnh. Vui lòng thử lại.");
            return showEditProfile(userDetails, model);
        }
    }

}