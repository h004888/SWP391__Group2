package com.OLearning.dto.login;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDTO {
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    //    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
//            message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    @NotBlank(message = "Password is required")
    @Size(min = 3, message = "Password must be at least 3 characters long")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    private boolean agreeToTerms = false;

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordsMatch() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

}
