package com.OLearning.service.adminDashBoard.impl;

import com.OLearning.dto.adminDashBoard.UserDTO;
import com.OLearning.dto.adminDashBoard.UserDetailDTO;
import com.OLearning.dto.login.RegisterDTO;
import com.OLearning.entity.Role;
import com.OLearning.entity.User;
import com.OLearning.mapper.adminDashBoard.UserDetailMapper;
import com.OLearning.mapper.adminDashBoard.UserMapper;
import com.OLearning.mapper.login.RegisterMapper;
import com.OLearning.repository.adminDashBoard.RoleRepository;
import com.OLearning.repository.adminDashBoard.UserRepository;
import com.OLearning.service.adminDashBoard.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserDetailMapper userDetailMapper;
    @Autowired
    private RegisterMapper registerMapper;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDetailDTO> getInfoUser(Long id) {
        return userRepository.findById(id)
                .map(userDetailMapper::toDetailDTO);
    }

    @Override
    public List<Role> getListRole() {
        return roleRepository.findAll();
    }

    @Override
    public boolean deleteAcc(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public User registerAccount(RegisterDTO registerDTO) {
        System.out.println("Starting registration for: " + registerDTO.getEmail());

        validateRegistrationData(registerDTO);
        System.out.println("Validation passed");

        User user = registerMapper.toUser(registerDTO);
        System.out.println("User mapped: " + user.getEmail());

        try {
            User savedUser = userRepository.save(user);
            System.out.println("User saved successfully with ID: " + savedUser.getUserId());
            return savedUser;
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void validateRegistrationData(RegisterDTO registrationDto) {
        // Check if passwords match
        if (!registrationDto.isPasswordsMatch()) {
            throw new RuntimeException("Passwords do not match");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email address is already registered");
        }

        // Check terms agreement
        if (!registrationDto.isAgreeToTerms()) {
            throw new RuntimeException("You must agree to the Terms of Service and Privacy Policy");
        }
    }
    @Override
    public List<UserDTO> searchByName(String keyword, Integer roleId) {
        if (roleId != null && roleId == 0) {
            roleId = null;
        }
        String processedKeyword = null;
        if (keyword != null && !keyword.trim().isEmpty()) {
            processedKeyword = keyword.trim().toLowerCase();
        }
        return userRepository.searchByKeyword(processedKeyword, roleId).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean resetPassword(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return false;
        }
        User user = optionalUser.get();

        //Defaut reset password is 12345
        String encodedPassword = new BCryptPasswordEncoder().encode("123");
        user.setPassword(encodedPassword);
        userRepository.save(user);
        //sau khi doi pass gửi email cho user bt
        return false;
    }


}
