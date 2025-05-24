package com.OLearning.service.adminDashBoard;

import com.OLearning.dto.adminDashBoard.UserDTO;
import com.OLearning.dto.adminDashBoard.UserDetailDTO;
import com.OLearning.entity.Role;
import com.OLearning.entity.User;
import com.OLearning.mapper.adminDashBoard.UserDetailMapper;
import com.OLearning.mapper.adminDashBoard.UserMapper;
import com.OLearning.repository.adminDashBoard.RoleRepo;
import com.OLearning.repository.adminDashBoard.UserRepo;
import lombok.RequiredArgsConstructor;
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

    public User createUser(User user) {
        return userRepo.save(user);
    }

    public List<Role> getListRole() {
        return roleRepo.findAll();
    }

    public boolean deleteAcc(Long id) {
        if(userRepo.existsById(id)){
            userRepo.deleteById(id);
            return true;
        }
        return false;
    }
}
