package com.OLearning.service.courseMaintenance.impl;

import com.OLearning.entity.Course;
import com.OLearning.entity.CourseMaintenance;
import com.OLearning.entity.Fee;
import com.OLearning.repository.*;
import com.OLearning.service.courseMaintenance.CourseMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class CourseMaintenanceServiceImpl implements CourseMaintenanceService{
    @Autowired
    private CourseMaintenanceRepository courseMaintenanceRepository;


    @Override
    public List<CourseMaintenance> getAllCourseMaintenances() {
        return courseMaintenanceRepository.findAll();
    }

    @Override
    public List<CourseMaintenance> searchByUsername(String username) {
        if (username != null && !username.isEmpty()) {
            return courseMaintenanceRepository.findByCourseInstructorUsernameContaining(username);
        }
        return courseMaintenanceRepository.findAll();
    }




}
