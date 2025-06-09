package com.OLearning.repository;

import com.OLearning.entity.BuyPackages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InstructorBuyPackagesRepository extends JpaRepository<BuyPackages, Long> {
    //query find package_id with user_id
    @Query(value = "SELECT bp.PackageId FROM BuyPackages bp " +
            "JOIN Packages p ON bp.PackageId = p.PackageId " +
            "WHERE bp.UserId = :userId " +
            "  AND bp.Status = 'Active' " +
            "  AND GETDATE() BETWEEN bp.ValidFrom AND bp.ValidTo ", nativeQuery = true)
    List<Object[]> findValidPackagesByUserId(@Param("userId") Long userId);
}


//khi dang ki xong can tim trong buyPackage xem no dang ki goi nao
