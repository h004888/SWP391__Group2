package com.OLearning.mapper.user;

import com.OLearning.dto.user.UserDTO;
import com.OLearning.entity.Role;
import com.OLearning.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRoleName(user.getRole().getName());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setStatus(user.getStatus());
        dto.setPersonalSkill(user.getPersonalSkill());
        dto.setCourse(user.getCourses());
        dto.setFullName(user.getFullName());
        return dto;
    }

    public User toUser(UserDTO dto, Role role) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setUsername(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setRole(role);
        user.setProfilePicture(dto.getProfilePicture());
        user.setProfilePicture(dto.getProfilePicture());
        user.setCourses(dto.getCourse());
        user.setStatus(true);
        return user;
    }
}
