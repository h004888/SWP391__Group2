package com.OLearning.dto.enrollment;

import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.user.UserDTO;
import com.OLearning.dto.user.UserEnrollmentDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private int enrollmentId;
    private UserEnrollmentDTO user;
    private LocalDateTime enrollmentDate;
    private double progress;
    private String status;
    private Long courseId;
    private String userProfile;
    private String username;
    private String courseTitle;
}
