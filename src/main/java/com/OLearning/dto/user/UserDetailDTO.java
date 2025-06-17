package com.OLearning.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String roleName;
    private String address;
    private String profilePicture;

    private List<String> enrolledCourseTitles;
}
