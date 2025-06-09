package com.OLearning.service.user;

import com.OLearning.dto.user.UserDTO;
import com.OLearning.dto.user.UserDetailDTO;
import com.OLearning.entity.Role;
import com.OLearning.entity.User;
import com.OLearning.mapper.user.UserDetailMapper;
import com.OLearning.mapper.user.UserMapper;
import com.OLearning.repository.RoleRepo;
import com.OLearning.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserDetailMapper userDetailMapper;

    public List<UserDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDetailDTO> getInfoUser(Long id) {
        return userRepo.findById(id)
                .map(userDetailMapper::toDetailDTO);
    }


    public User userDTOtoUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUserName());
        user.setEmail(userDTO.getEmail());
        //default data
        user.setFullName(userDTO.getUserName());
        user.setPassword("123");

        user.setRole(roleRepo.findRoleByName(userDTO.getRoleName()));

        return user;
    }

    public User createUser(User user) {
        return userRepo.save(user);
    }

    public List<Role> getListRole() {
        return roleRepo.findAll();
    }

    public void deleteAcc(Long id) {
        userRepo.deleteById(id);
    }
}
