package com.OLearning.service.adminDashBoard;

import com.OLearning.entity.BuyPackages;
import com.OLearning.repository.adminDashBoard.BuyPackagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuyPackagesService {
    @Autowired
    private BuyPackagesRepository buyPackagesRepository;

    public List<BuyPackages> getAllBuyPackages() {
        return buyPackagesRepository.findAll();
    }
}
