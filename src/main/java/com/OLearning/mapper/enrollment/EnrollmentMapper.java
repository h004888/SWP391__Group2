package com.OLearning.mapper.enrollment;

import com.OLearning.dto.enrollment.EnrollmentDTO;
import com.OLearning.dto.user.UserDTO;
import com.OLearning.dto.user.UserEnrollmentDTO;
import com.OLearning.entity.Enrollment;
import com.OLearning.mapper.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentMapper {
    @Autowired
    private UserMapper userMapper;

    public EnrollmentDTO MapEnrollmentDTO(Enrollment enrollment) {
        EnrollmentDTO enrollmentDTO = new EnrollmentDTO();
        enrollmentDTO.setEnrollmentId(enrollment.getEnrollmentId());
        enrollmentDTO.setUserProfile(enrollment.getUser().getProfilePicture());
        enrollmentDTO.setUsername(enrollment.getUser().getUsername());
        enrollmentDTO.setCourseTitle(enrollment.getCourse().getTitle());
        enrollmentDTO.setEnrollmentDate(enrollment.getEnrollmentDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        enrollmentDTO.setProgress(enrollment.getProgress());
        enrollmentDTO.setStatus(enrollment.getStatus());
        enrollmentDTO.setCourseId(enrollment.getCourse().getCourseId());
        // Thêm thông tin user
        UserEnrollmentDTO userDTO = new UserEnrollmentDTO();
        userDTO.setUserId(enrollment.getUser().getUserId());
        userDTO.setUsername(enrollment.getUser().getUsername());
        userDTO.setEmail(enrollment.getUser().getEmail());
        userDTO.setProfilePicture(enrollment.getUser().getProfilePicture());
        userDTO.setFullName(enrollment.getUser().getFullName());
        userDTO.setAddress(enrollment.getUser().getAddress());
        enrollmentDTO.setUser(userDTO);

        return enrollmentDTO;
    }
}
