package com.OLearning.repository;

import com.OLearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorUserRepo extends JpaRepository<User, Long> {

}
