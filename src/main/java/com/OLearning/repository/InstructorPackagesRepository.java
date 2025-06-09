package com.OLearning.repository;

import com.OLearning.entity.Packages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorPackagesRepository extends JpaRepository<Packages, Long> {
}
