package com.OLearning.service.adminDashBoard;

import com.OLearning.dto.adminDashBoard.UserDTO;
import com.OLearning.dto.adminDashBoard.UserDetailDTO;
import com.OLearning.dto.login.RegisterDTO;
import com.OLearning.entity.Role;
import com.OLearning.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    List<UserDTO> getAllUsers();

    Optional<UserDetailDTO> getInfoUser(Long id);

    List<Role> getListRole();

    boolean deleteAcc(Long id);

    List<UserDTO> searchByName(String keyword, Integer roleId);

    boolean resetPassword(Long id);

    User registerAccount(RegisterDTO registerDTO);

    void validateRegistrationData(RegisterDTO registrationDto);
}
