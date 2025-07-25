package com.OLearning.service.user.impl;

import com.OLearning.dto.user.UserDTO;
import com.OLearning.dto.user.UserDetailDTO;
import com.OLearning.dto.user.UserProfileUpdateDTO;
import com.OLearning.dto.login.RegisterDTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.entity.Enrollment;
import com.OLearning.entity.Role;
import com.OLearning.entity.User;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.mapper.user.UserDetailMapper;
import com.OLearning.mapper.user.UserMapper;
import com.OLearning.mapper.login.RegisterMapper;
import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.repository.RoleRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.email.EmailService;
import com.OLearning.service.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.OLearning.dto.user.UserProfileEditDTO;

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
    @Autowired
    private EmailService emailService;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public Page<UserDTO> getAllUsers(Pageable page) {
        Page<User> users = userRepository.findAll(page);
        return users.map(userMapper::toDTO);
    }

    @Override
    public Page<UserDTO> getUsersByRoleWithPagination(Long roleId, Pageable pageable) {
        Page<User> userPage = userRepository.findByRole_RoleId(roleId, pageable);
        return userPage.map(userMapper::toDTO);
    }

    @Override
    public Page<UserDTO> getInstructorsByRoleIdOrderByCourseCountDesc(Long roleId, Pageable pageable) {
        Page<User> userPage = userRepository.findInstructorsByRoleIdOrderByCourseCountDesc(roleId, pageable);
        return userPage.map(userMapper::toDTO);
    }

    @Override
    public Page<UserDTO> searchByNameWithPagination(String keyword, Long roleId, Pageable pageable) {
        // Sử dụng repository method với pagination
        Page<User> userPage = userRepository.findByUsernameContainingIgnoreCaseAndRole_RoleId(keyword, roleId, pageable);
        return userPage.map(userMapper::toDTO);
    }

    @Override
    public Page<UserDTO> getInstructorsByRoleIdAndKeywordOrderByCourseCountDesc(String keyword, Long roleId, Pageable pageable) {
        Page<User> userPage = userRepository.findInstructorsByRoleIdAndKeywordOrderByCourseCountDesc(roleId, keyword, pageable);
        return userPage.map(userMapper::toDTO);
    }

    @Override
    public Page<UserDTO> getUsersByRoleAndStatusWithPagination(Long roleId, boolean status, Pageable pageable) {
        Page<User> userPage = userRepository.findByRole_RoleIdAndStatus(roleId, status, pageable);
        return userPage.map(userMapper::toDTO);
    }

    @Override
    public Page<UserDTO> searchByNameAndStatusWithPagination(String keyword, Long roleId, boolean status, Pageable pageable) {
        Page<User> userPage = userRepository.findByUsernameContainingIgnoreCaseAndRole_RoleIdAndStatus(keyword, roleId, status, pageable);
        return userPage.map(userMapper::toDTO);
    }

    @Override
    public Optional<UserDetailDTO> getInfoUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();
        UserDetailDTO userDetailDTO = userDetailMapper.toDetailDTO(user);

        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByUserId(user.getUserId());
        List<CourseDTO> enrolledCourses = enrollments.stream()
                .map(Enrollment::getCourse)
                .map(courseMapper::MapCourseDTO)
                .collect(Collectors.toList());

        userDetailDTO.setEnrolledCourses(enrolledCourses);

        return Optional.of(userDetailDTO);
    }

    @Override
    public List<Role> getListRole() {
        return roleRepository.findAll();
    }

    @Override
    public boolean changStatus(Long id) {
        if (userRepository.existsById(id)) {
            Optional<User> user = userRepository.findById(id);
            try {
                emailService.sendAccountStatusEmail(user.get(), user.get().getStatus());
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            user.get().setStatus(!user.get().getStatus());
            userRepository.save(user.get());

            return true;
        }
        return false;
    }

    @Override
    public User registerAccount(RegisterDTO registerDTO) {
        validateRegistrationData(registerDTO);
        User user = registerMapper.toUser(registerDTO);

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
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email address is already registered");
        }
        // Check terms agreement
        if (!registrationDto.isAgreeToTerms()) {
            throw new RuntimeException("You must agree to the Terms of Service and Privacy Policy");
        }
    }

    @Override
    public void assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findRoleByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public List<UserDTO> getUsersByRole(Long roleId) {
        return userRepository.findByRole_RoleId(roleId).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public User createNewStaff(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        Optional<Role> roleOpt = roleRepository.findRoleByName(userDTO.getRoleName());
        if (roleOpt.isEmpty()) {
            throw new IllegalArgumentException("Role not found: " + userDTO.getRoleName());
        }
        User user = userMapper.toUser(userDTO, roleOpt.get());
        // Set default avatar if not provided
        if (user.getProfilePicture() == null || user.getProfilePicture().trim().isEmpty()) {
            user.setProfilePicture("/img/undraw_profile.svg");
        }
        //Default password
        String encodedPassword = new BCryptPasswordEncoder().encode("123");
        user.setPassword(encodedPassword);

        //Send notification email to new staff
        emailService.sendPromotedToStaffEmail(user);

        return userRepository.save(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return userMapper.toDTO(user);
    }

    @Override
    public User findById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return null;
        }
        return userOptional.get();
    }

    @Override
    public boolean deleteAcc(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()) {
            return false;
        }
        userRepository.deleteById(id);
        return false;
    }

    @Override
    public boolean resetPassword(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return false;
        }
        User user = optionalUser.get();

        //Defaut reset password is 123
        String encodedPassword = new BCryptPasswordEncoder().encode("123");
        user.setPassword(encodedPassword);
        userRepository.save(user);
        //sau khi doi pass gửi email cho user bt
        return false;
    }

    @Override
    public List<UserDTO> getTopInstructorsByCourseCount(int limit) {
        // Get all instructors (roleId = 2)
        List<User> instructors = userRepository.findByRoleId(2L);
        
        // Sort instructors by number of courses in descending order
        return instructors.stream()
                .sorted((i1, i2) -> Integer.compare(
                        i2.getCourses().size(),
                        i1.getCourses().size()
                ))
                .limit(limit)
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserDTO> filterInstructors(String keyword, Pageable pageable) {
        Page<User> userPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            userPage = userRepository.findInstructorsByRoleIdAndKeywordOrderByCourseCountDesc(2L, keyword.trim(), pageable);
        } else {
            userPage = userRepository.findInstructorsByRoleIdOrderByCourseCountDesc(2L, pageable);
        }
        return userPage.map(userMapper::toDTO);
    }

    @Override
    public void updatePasswordByEmail(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with email: " + email);
        }
    }


    @Override
    public boolean existsById(Long userId) {

        return userRepository.existsById(userId);
    }

    @Override
    public Optional<UserProfileEditDTO> getProfileByUsername(String username) {
        return userRepository.findByEmail(username).map(user -> {
            UserProfileEditDTO dto = new UserProfileEditDTO();
            dto.setFullName(user.getFullName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            dto.setAvatarUrl(user.getProfilePicture());
            dto.setUsername(user.getUsername());
            dto.setBirthDay(user.getBirthDay());
            dto.setAddress(user.getAddress());
            dto.setPersonalSkill(user.getPersonalSkill());
            return dto;
        });
    }

    @Override
    public void updateProfileByUsername(String username, UserProfileEditDTO profileEditDTO) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + username));
        user.setFullName(profileEditDTO.getFullName());
        user.setPhone(profileEditDTO.getPhone());
        user.setProfilePicture(profileEditDTO.getAvatarUrl());
        user.setUsername(profileEditDTO.getUsername());
        user.setBirthDay(profileEditDTO.getBirthDay());
        user.setAddress(profileEditDTO.getAddress());
        user.setPersonalSkill(profileEditDTO.getPersonalSkill());
        userRepository.save(user);
    }

    @Override
    public void updateProfile(Long userId, UserProfileEditDTO profileEditDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setFullName(profileEditDTO.getFullName());
        user.setPhone(profileEditDTO.getPhone());
        user.setProfilePicture(profileEditDTO.getAvatarUrl());
        user.setUsername(profileEditDTO.getUsername());
        user.setBirthDay(profileEditDTO.getBirthDay());
        user.setAddress(profileEditDTO.getAddress());
        user.setPersonalSkill(profileEditDTO.getPersonalSkill());
        userRepository.save(user);
    }

    @Override
    public User updateProfile(Long userId, UserProfileUpdateDTO profileUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Validate that user can only update their own profile
        if (!userId.equals(profileUpdateDTO.getUserId())) {
            throw new IllegalArgumentException("You can only update your own profile");
        }

        // Update profile information with validation
        if (profileUpdateDTO.getFullName() != null && !profileUpdateDTO.getFullName().trim().isEmpty()) {
            user.setFullName(profileUpdateDTO.getFullName().trim());
        }

        if (profileUpdateDTO.getPhone() != null && !profileUpdateDTO.getPhone().trim().isEmpty()) {
            // Validate phone format
            if (!profileUpdateDTO.getPhone().matches("^[0-9]{10,15}$")) {
                throw new IllegalArgumentException("Invalid phone number format");
            }
            user.setPhone(profileUpdateDTO.getPhone().trim());
        }

        if (profileUpdateDTO.getBirthDay() != null) {
            // Validate birthday is in the past
            if (profileUpdateDTO.getBirthDay().isAfter(java.time.LocalDate.now())) {
                throw new IllegalArgumentException("Birthday must be in the past");
            }
            user.setBirthDay(profileUpdateDTO.getBirthDay());
        }

        if (profileUpdateDTO.getAddress() != null) {
            user.setAddress(profileUpdateDTO.getAddress().trim());
        }

        if (profileUpdateDTO.getPersonalSkill() != null) {
            user.setPersonalSkill(profileUpdateDTO.getPersonalSkill().trim());
        }

        if (profileUpdateDTO.getProfilePicture() != null) {
            user.setProfilePicture(profileUpdateDTO.getProfilePicture());
            // Đánh dấu đây là ảnh custom, không phải từ Google
            user.setIsGooglePicture(false);
        }

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update profile: " + e.getMessage());
        }
    }

    @Override
    public User updateProfilePicture(Long userId, String newProfilePictureUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        user.setProfilePicture(newProfilePictureUrl);
        // Đánh dấu đây là ảnh custom, không phải từ Google
        user.setIsGooglePicture(false);
        return userRepository.save(user);
    }

}
