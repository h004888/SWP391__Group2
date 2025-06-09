package com.OLearning.mapper.user;

import com.OLearning.dto.user.UserDetailDTO;
import com.OLearning.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserDetailMapper {

    public UserDetailDTO toDetailDTO(User user) {
        UserDetailDTO dto = new UserDetailDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setRoleName(user.getRole().getName());
//        dto.setEnrolledCourseTitles(
//                user.getCourses().stream()
//                        .map(Course::getTitle)
//                        .collect(Collectors.toList())
//        );
        return dto;
    }

}
