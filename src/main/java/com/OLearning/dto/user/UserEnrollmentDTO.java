package com.OLearning.dto.user;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEnrollmentDTO {
    private Long userId;
    private String username;
    private String email;
    private String profilePicture;
    private String fullName;
    private String address;
}
