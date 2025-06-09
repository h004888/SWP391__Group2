package com.OLearning.mapper.User;

import com.OLearning.dto.user.UserDTO;
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
        return dto;
    }
}
