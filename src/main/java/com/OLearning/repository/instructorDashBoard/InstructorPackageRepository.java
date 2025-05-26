package com.OLearning.repository.instructorDashBoard;

import com.OLearning.entity.BuyPackages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InstructorPackageRepository extends JpaRepository<BuyPackages, Long> {
    @Query(value = "SELECT * FROM BuyPackages bp " +
            "JOIN Packages p ON bp.PackageId = p.PackageId " +
            "WHERE bp.UserId = :userId " +
            "  AND bp.Status = 'Active' " +
            "  AND GETDATE() BETWEEN bp.ValidFrom AND bp.ValidTo " +
            "  AND p.CoursesCreated < p.MaxCourse", nativeQuery = true)
    List<Object[]> findValidPackagesByUserId(@Param("userId") Long userId);
}
