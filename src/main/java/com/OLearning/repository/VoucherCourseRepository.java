package com.OLearning.repository;

import com.OLearning.entity.VoucherCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherCourseRepository extends JpaRepository<VoucherCourse, Long> {

}
