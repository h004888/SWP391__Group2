package com.OLearning.mapper.login;

import com.OLearning.dto.login.RegisterDTO;
import com.OLearning.entity.Role;
import com.OLearning.entity.User;
import com.OLearning.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class RegisterMapper {

    @Autowired
    private  RoleRepository roleRepository;

    public User toUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setFullName(registerDTO.getFirstName() + " " + registerDTO.getLastName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(registerDTO.getPassword()));
        Role roleUser = roleRepository.findRoleByNameIsIgnoreCase("User")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        user.setRole(roleUser);
        user.setStatus(true); 
        return user;
    }

}
