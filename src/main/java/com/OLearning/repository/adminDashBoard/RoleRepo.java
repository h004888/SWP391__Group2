package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role,Long> {
Role findRoleByName(String roleName);
}
