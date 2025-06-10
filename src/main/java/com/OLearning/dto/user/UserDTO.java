package com.OLearning.dto.user;

import com.OLearning.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long userId;
    private String userName;
    private String email;
    private String roleName;
    private String profilePicture;
    private String personalSkill;
    private boolean status;
    private List<Course> course;

}