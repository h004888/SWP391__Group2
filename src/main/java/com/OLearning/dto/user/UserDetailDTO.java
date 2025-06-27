package com.OLearning.dto.user;

import com.OLearning.dto.course.CourseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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
    private Double coin;
    private String roleName;
    private String address;
    private LocalDate birthDay;
    private String profilePicture;
    private String personalSkill;
    private List<CourseDTO> enrolledCourses;
}
