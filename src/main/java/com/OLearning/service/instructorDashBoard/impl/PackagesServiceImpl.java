package com.OLearning.service.instructorDashBoard.impl;

import com.OLearning.entity.Packages;
import com.OLearning.repository.instructorDashBoard.InstructorPackagesRepository;
import com.OLearning.service.instructorDashBoard.PackagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class PackagesServiceImpl implements PackagesService {
    @Autowired
    InstructorPackagesRepository packagesRepository;

    @Override
    public void updateCourseCreated(Long id) {
        Optional<Packages> existingPackagesOpt = packagesRepository.findById(id);
        if(existingPackagesOpt.isPresent()) {
            Packages packagesUpdate = existingPackagesOpt.get();
            packagesUpdate.setCoursesCreated(packagesUpdate.getCoursesCreated() + 1);
            packagesRepository.save(packagesUpdate);
        }
    }
}
