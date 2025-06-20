package com.OLearning.service.user;

import com.OLearning.dto.user.UserDTO;
import com.OLearning.dto.user.UserDetailDTO;
import com.OLearning.dto.login.RegisterDTO;
import com.OLearning.entity.Role;
import com.OLearning.entity.User;

import org.mapstruct.control.MappingControl.Use;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    Page<UserDTO> getAllUsers(Pageable pageable);

    Page<UserDTO> getUsersByRoleWithPagination(Long roleId, Pageable pageable);

    Page<UserDTO> getInstructorsByRoleIdOrderByCourseCountDesc(Long roleId, Pageable pageable);

    Page<UserDTO> searchByNameWithPagination(String keyword, Long roleId, Pageable pageable);

    Page<UserDTO> getInstructorsByRoleIdAndKeywordOrderByCourseCountDesc(String keyword, Long roleId,
            Pageable pageable);

    Page<UserDTO> getUsersByRoleAndStatusWithPagination(Long roleId, boolean status, Pageable pageable);

    Page<UserDTO> searchByNameAndStatusWithPagination(String keyword, Long roleId, boolean status, Pageable pageable);

    Optional<UserDetailDTO> getInfoUser(Long id);

    List<Role> getListRole();

    List<UserDTO> getUsersByRole(Long roleId);

    User createNewStaff(UserDTO userDTO);

    boolean deleteAcc(Long id);

    boolean changStatus(Long id);

    // List<UserDTO> searchByName(String keyword, Long roleId);

    boolean resetPassword(Long id);

    User registerAccount(RegisterDTO registerDTO);

    void validateRegistrationData(RegisterDTO registrationDto);

    void assignRoleToUser(Long userId, String roleName);

    List<UserDTO> getTopInstructorsByCourseCount(int limit);

    UserDTO getUserByEmail(String username);

    Page<UserDTO> filterInstructors(String keyword, Pageable pageable);

    Optional<User> findById(Long userId);

    boolean existsById(Long userId);
}