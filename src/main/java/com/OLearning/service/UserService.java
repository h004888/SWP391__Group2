package com.OLearning.service;

import com.OLearning.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface UserService {
    UserDTO getUserById(int id);

    List<UserDTO> getAllUsers();

    void createUser(UserDTO userDTO);

    void updateUser(UserDTO userDTO);

    void deleteUser(int id);

    UserDTO findByEmail(String email);

    UserDTO findByFullName(String fullName);

}
