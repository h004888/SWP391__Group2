package com.OLearning.service.user.Impl;

import com.OLearning.dto.adminDashBoard.UserDTO;
import com.OLearning.dto.adminDashBoard.UserDetailDTO;
import com.OLearning.entity.Role;
import com.OLearning.entity.User;
import com.OLearning.mapper.User.UserDetailMapper;
import com.OLearning.mapper.User.UserMapper;
import com.OLearning.repository.RoleRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
    public User userDTOtoUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("dupliacte user");
        }
        User user = new User();
        user.setUsername(userDTO.getUserName());
        user.setEmail(userDTO.getEmail());
        //default data
        user.setFullName(userDTO.getUserName());
        //encrypt password
        user.setPassword(new BCryptPasswordEncoder().encode("123"));

        user.setRole(roleRepository.findRoleByName(userDTO.getRoleName()));

        return user;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
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



}
