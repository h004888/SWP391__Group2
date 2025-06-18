package com.OLearning.service.adminDashBoard;

import com.OLearning.dto.user.UserDTO;
import com.OLearning.dto.user.UserDetailDTO;
import com.OLearning.entity.Role;
import com.OLearning.entity.User;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    List<UserDTO> getAllUsers();

    Optional<UserDetailDTO> getInfoUser(Long id);

    User userDTOtoUser(UserDTO userDTO);

    User createUser(User user);

    List<Role> getListRole();

    boolean deleteAcc(Long id);

    List<UserDTO> searchByName(String keyword,Integer roleId);

}
