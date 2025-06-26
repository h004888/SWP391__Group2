package com.OLearning.mapper.user;

import com.OLearning.dto.user.UserDetailDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.User;
import com.OLearning.mapper.course.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserDetailMapper {

    @Autowired
    private CourseMapper courseMapper;
    public UserDetailDTO toDetailDTO(User user) {
        UserDetailDTO dto = new UserDetailDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setCoin(user.getCoin());
        dto.setAddress(user.getAddress());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setRoleName(user.getRole().getName());
        dto.setBirthDay(user.getBirthDay());
        dto.setPersonalSkill(user.getPersonalSkill());
        dto.setEnrolledCourses(
                user.getCourses().stream()
                        .map(courseMapper::MapCourseDTO)
                        .collect(Collectors.toList())
        );
        return dto;
    }

}