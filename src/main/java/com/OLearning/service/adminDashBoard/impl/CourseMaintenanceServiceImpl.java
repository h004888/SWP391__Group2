package com.OLearning.service.adminDashBoard.impl;

import com.OLearning.entity.CourseMaintenance;
import com.OLearning.service.adminDashBoard.CourseMaintenanceService;
import com.OLearning.repository.adminDashBoard.CourseMaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Override
    public List<CourseMaintenance> filterByMonthYear(String monthYear) {
        if (monthYear != null && !monthYear.isEmpty()) {
            LocalDate parsedMonthYear = LocalDate.parse(monthYear + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return courseMaintenanceRepository.findByMonthYear(parsedMonthYear);
        }
        return courseMaintenanceRepository.findAll();
    }

    @Override
    public List<LocalDate> getDistinctMonthYears() {
        return courseMaintenanceRepository.findDistinctMonthYears();
    }
}
