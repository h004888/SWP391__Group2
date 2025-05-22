package com.OLearning.repository.adminDashBoard;

import com.OLearning.entity.Role;
import com.OLearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
}
