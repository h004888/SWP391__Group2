package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.BuyPackages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyPackagesRepository extends JpaRepository<BuyPackages, Long> {

}
