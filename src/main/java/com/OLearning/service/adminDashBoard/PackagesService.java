package com.OLearning.service.adminDashBoard;

import com.OLearning.entity.Packages;
import com.OLearning.repository.adminDashBoard.PackagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackagesService {
    @Autowired
    private PackagesRepository packagesRepository;

    public List<Packages> getAllPackages() {
        return packagesRepository.findAll();
    }
}
