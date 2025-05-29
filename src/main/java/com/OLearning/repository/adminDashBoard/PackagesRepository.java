package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.Packages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackagesRepository extends JpaRepository<Packages, Integer> {

}
