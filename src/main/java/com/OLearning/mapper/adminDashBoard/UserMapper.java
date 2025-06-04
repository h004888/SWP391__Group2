package com.OLearning.mapper.adminDashBoard;

import com.OLearning.dto.adminDashBoard.UserDTO;
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
        dto.setStatus(user.isStatus());
        return dto;
    }

    public User toUser(UserDTO dto, Role role) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setUsername(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setRole(role);
        user.setStatus(true);
        return user;
    }
}
