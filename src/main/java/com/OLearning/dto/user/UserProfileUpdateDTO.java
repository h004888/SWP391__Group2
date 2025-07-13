package com.OLearning.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateDTO {
    private Long userId;
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;
    
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be between 10-15 digits")
    private String phone;
    
    @Past(message = "Birthday must be in the past")
    private LocalDate birthDay;
    
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;
    
    private String profilePicture;
    
    private Boolean isGooglePicture;
    
    @Size(max = 1000, message = "Personal skills cannot exceed 1000 characters")
    private String personalSkill;
} 