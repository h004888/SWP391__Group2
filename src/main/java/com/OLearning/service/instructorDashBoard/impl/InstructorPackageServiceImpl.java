package com.OLearning.service.instructorDashBoard.impl;

import com.OLearning.repository.instructorDashBoard.InstructorBuyPackagesRepository;
import com.OLearning.service.instructorDashBoard.PackagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InstructorPackageServiceImpl implements PackagesService {
@Autowired
private InstructorBuyPackagesRepository instructorBuyPackagesRepository;
    @Override
    public Integer getPackageIdFormBuyPackages(Long userId) {
        return null;
    }
}
