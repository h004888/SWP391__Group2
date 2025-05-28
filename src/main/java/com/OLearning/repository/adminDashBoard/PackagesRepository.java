package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.Packages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackagesRepository extends JpaRepository<Packages, Integer> {

}
