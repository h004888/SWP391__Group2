package com.OLearning.service.adminDashBoard.impl;

import com.OLearning.dto.adminDashBoard.UserDTO;
import com.OLearning.dto.adminDashBoard.UserDetailDTO;
import com.OLearning.entity.Role;
import com.OLearning.entity.User;
import com.OLearning.mapper.adminDashBoard.UserDetailMapper;
import com.OLearning.mapper.adminDashBoard.UserMapper;
import com.OLearning.repository.adminDashBoard.RoleRepo;
import com.OLearning.repository.adminDashBoard.UserRepo;
import com.OLearning.service.adminDashBoard.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserDetailMapper userDetailMapper;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDetailDTO> getInfoUser(Long id) {
        return userRepo.findById(id)
                .map(userDetailMapper::toDetailDTO);
    }

    @Override
    public User userDTOtoUser(UserDTO userDTO) {
        if (userRepo.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("dupliacte user");
        }
        User user = new User();
        user.setUsername(userDTO.getUserName());
        user.setEmail(userDTO.getEmail());
        //default data
        user.setFullName(userDTO.getUserName());
        user.setPassword("123");

        user.setRole(roleRepo.findRoleByName(userDTO.getRoleName()));

        return user;
    }

    @Override
    public User createUser(User user) {
        return userRepo.save(user);
    }

    @Override
    public List<Role> getListRole() {
        return roleRepo.findAll();
    }

    @Override
    public boolean deleteAcc(Long id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
            return true;
        }
        return false;
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
        return userRepo.searchByKeyword(processedKeyword, roleId).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

}
