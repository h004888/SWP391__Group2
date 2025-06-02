package com.OLearning.service.instructorDashboard.impl;

import com.OLearning.repository.instructorDashBoard.InstructorBuyPackagesRepository;
import com.OLearning.service.instructorDashboard.InstructorBuyPackagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstructorBuyPackagesServiceImpl implements InstructorBuyPackagesService {
    @Autowired
    private InstructorBuyPackagesRepository buyPackagesRepository;
    @Override
    public Long findIdPackages(Long userId) {
        List<Long> listPackagesId = buyPackagesRepository.findValidPackagesByUserId(userId);
        return listPackagesId.isEmpty() ? null : listPackagesId.get(0);
    }
}
