package com.OLearning.service.voucher.impl;

import com.OLearning.dto.voucher.UserVoucherDTO;
import com.OLearning.dto.voucher.VoucherDTO;
import com.OLearning.entity.*;
import com.OLearning.mapper.voucher.UserVoucherMapper;
import com.OLearning.mapper.voucher.VoucherMapper;
import com.OLearning.repository.UserRepository;
import com.OLearning.repository.UserVoucherRepository;
import com.OLearning.repository.VoucherRepository;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.repository.VoucherCourseRepository;
import com.OLearning.service.notification.NotificationService;
import com.OLearning.service.voucher.VoucherService;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.dto.voucher.VoucherStatsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private UserVoucherRepository userVoucherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoucherMapper voucherMapper;

    @Autowired
    private UserVoucherMapper userVoucherMapper;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private VoucherCourseRepository voucherCourseRepository;

    @Autowired
    private NotificationService notificationService;



    @Override
    @Transactional
    public VoucherDTO applyVoucher(String code, Long userId) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        if (!voucher.getIsActive() || voucher.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Voucher is invalid or expired");
        }
        if (voucher.getLimitation() != null && voucher.getUsedCount() >= voucher.getLimitation()) {
            throw new RuntimeException("Voucher usage limit reached");
        }
        java.util.Optional<UserVoucher> existing = userVoucherRepository.findByUser_UserIdAndVoucher_VoucherId(userId, voucher.getVoucherId());
        if (existing.isPresent()) {
            throw new RuntimeException("You have already entered this voucher!");
        }
        // Chặn nếu user đã mua hết các course mà voucher áp dụng
        List<Course> courses = voucher.getVoucherCourses() == null ? List.of() : voucher.getVoucherCourses().stream().map(vc -> vc.getCourse()).toList();
        boolean allPurchased = courses.stream().allMatch(course -> enrollmentRepository.existsByUser_UserIdAndCourse_CourseId(userId, course.getCourseId()));
        if (!courses.isEmpty() && allPurchased) {
            throw new RuntimeException("You have already purchased all courses for this voucher. Cannot claim or enter this voucher.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserVoucher userVoucher = new UserVoucher();
        userVoucher.setUser(user);
        userVoucher.setVoucher(voucher);
        userVoucher.setIsUsed(false);
        userVoucherRepository.save(userVoucher);
        voucher.setUsedCount(voucher.getUsedCount() + 1);
        voucherRepository.save(voucher);
        return voucherMapper.toVoucherDTO(voucher);
    }

    @Override
    public List<UserVoucherDTO> getUserVouchers(Long userId) {
        List<UserVoucher> userVouchers = userVoucherRepository.findByUser_UserIdAndIsUsedFalse(userId);
        LocalDate today = LocalDate.now();
        return userVouchers.stream()
                .filter(uv -> uv.getVoucher().getIsActive() != null && uv.getVoucher().getIsActive())
                .filter(uv -> uv.getVoucher().getExpiryDate() != null && !uv.getVoucher().getExpiryDate().isBefore(today))
                .map(userVoucherMapper::toUserVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserVoucherDTO> getUserVouchersSortedByLatest(Long userId) {
        List<UserVoucher> userVouchers = userVoucherRepository.findByUser_UserIdAndIsUsedFalseOrderByIdDesc(userId);
        LocalDate today = LocalDate.now();
        return userVouchers.stream()
                .filter(uv -> uv.getVoucher().getIsActive() != null && uv.getVoucher().getIsActive())
                .filter(uv -> uv.getVoucher().getExpiryDate() != null && !uv.getVoucher().getExpiryDate().isBefore(today))
                .map(userVoucherMapper::toUserVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> getValidVouchersForCourse(Long courseId) {
        List<Voucher> vouchers = voucherRepository.findValidVouchersForCourse(courseId);
        return vouchers.stream()
                .filter(v -> Boolean.TRUE.equals(v.getIsPublic()))
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VoucherDTO useVoucher(Long voucherId, Long userId, Long courseId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        UserVoucher userVoucher = userVoucherRepository.findByUser_UserIdAndIsUsedFalse(userId).stream()
                .filter(uv -> uv.getVoucher().getVoucherId().equals(voucherId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User does not have this voucher"));

        if (!voucher.getIsActive() || voucher.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Voucher is invalid or expired");
        }
        if (voucher.getLimitation() != null && voucher.getUsedCount() >= voucher.getLimitation()) {
            throw new RuntimeException("Voucher usage limit reached");
        }

        userVoucher.setIsUsed(true);
        userVoucher.setUsedDate(LocalDate.now());
        userVoucherRepository.save(userVoucher);

        return voucherMapper.toVoucherDTO(voucher);
    }

    @Override
    public List<UserVoucherDTO> getValidVouchersForCourseAndUser(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<UserVoucher> userVouchers = userVoucherRepository.findByUser_UserIdAndIsUsedFalse(userId);

        List<UserVoucher> validVouchers = userVouchers.stream()
                .filter(uv -> {
                    Voucher voucher = uv.getVoucher();

                    if (!voucher.getIsActive()) {
                        return false;
                    }

                    if (voucher.getExpiryDate().isBefore(LocalDate.now())) {
                        return false;
                    }

                    if (voucher.getLimitation() != null && voucher.getUsedCount() >= voucher.getLimitation()) {
                        return false;
                    }

                    // Kiểm tra áp dụng cho course này
                    if (voucher.getVoucherCourses() == null || voucher.getVoucherCourses().isEmpty()) {
                        return false;
                    }
                    return voucher.getVoucherCourses().stream()
                            .anyMatch(vc -> vc.getCourse().getCourseId().equals(courseId));
                })
                .collect(Collectors.toList());

        List<UserVoucherDTO> result = validVouchers.stream()
                .map(userVoucherMapper::toUserVoucherDTO)
                .collect(Collectors.toList());

        return result;
    }

    @Override
    @Transactional
    public void useVoucherForUserAndCourse(Long voucherId, Long userId) {
        UserVoucher userVoucher = userVoucherRepository.findByUser_UserIdAndVoucher_VoucherId(userId, voucherId)
                .orElseThrow(() -> new RuntimeException("UserVoucher not found"));
        if (Boolean.TRUE.equals(userVoucher.getIsUsed())) return;
        userVoucher.setIsUsed(true);
        userVoucher.setUsedDate(LocalDate.now());
        userVoucherRepository.save(userVoucher);
        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow(() -> new RuntimeException("Voucher not found"));
        voucherRepository.save(voucher);
    }

    @Override
    public List<CourseDTO> getValidCoursesForVoucherAndUser(Long voucherId, Long userId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        // Kiểm tra user có voucher này và chưa dùng
        boolean userHasVoucher = userVoucherRepository.findByUser_UserIdAndVoucher_VoucherId(userId, voucherId)
                .filter(uv -> !Boolean.TRUE.equals(uv.getIsUsed()))
                .isPresent();
        if (!userHasVoucher) return List.of();
        // Kiểm tra voucher còn hiệu lực
        if (!voucher.getIsActive() || voucher.getExpiryDate().isBefore(LocalDate.now())) {
            return List.of();
        }
        if (voucher.getLimitation() != null && voucher.getUsedCount() >= voucher.getLimitation()) {
            return List.of();
        }
        List<Course> courses;
        if (voucher.getVoucherCourses() == null || voucher.getVoucherCourses().isEmpty()) {
            courses = new ArrayList<>();
        } else {
            courses = voucher.getVoucherCourses().stream()
                    .map(vc -> vc.getCourse())
                    .toList();
        }
        // Lọc chỉ lấy course còn active và user chưa mua
        courses = courses.stream()
                .filter(course -> "publish".equalsIgnoreCase(course.getStatus()))
                .filter(course -> !enrollmentRepository.existsByUser_UserIdAndCourse_CourseId(userId, course.getCourseId()))
                .toList();
        return courses.stream()
                .map(CourseMapper::toDTO)
                .toList();
    }

    @Override
    public List<CourseDTO> getCoursesForVoucher(Long voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        List<Course> courses;
        if (voucher.getVoucherCourses() == null || voucher.getVoucherCourses().isEmpty()) {
            courses = new ArrayList<>();
        } else {
            courses = voucher.getVoucherCourses().stream()
                    .map(vc -> vc.getCourse())
                    .toList();
        }
        // Lọc chỉ lấy course còn active
        courses = courses.stream()
                .filter(course -> "publish".equalsIgnoreCase(course.getStatus()))
                .toList();
        return courses.stream()
                .map(CourseMapper::toDTO)
                .toList();
    }

    @Override
    public VoucherStatsDTO getVoucherStatsForInstructor(Long instructorId, String search) {
        List<Voucher> allVouchers = voucherRepository.findAll().stream()
                .filter(v -> v.getInstructor().getUserId().equals(instructorId))
                .toList();

        if (search != null && !search.isBlank()) {
            allVouchers = allVouchers.stream()
                    .filter(v -> v.getCode() != null && v.getCode().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }

        LocalDate now = LocalDate.now();
        List<Voucher> validVouchers = allVouchers.stream()
                .filter(v -> v.getIsActive() != null && v.getIsActive() && v.getExpiryDate() != null && v.getExpiryDate().isAfter(now))
                .toList();
        List<Voucher> expiredVouchers = allVouchers.stream()
                .filter(v -> v.getExpiryDate() == null || !v.getExpiryDate().isAfter(now) || Boolean.FALSE.equals(v.getIsActive()))
                .toList();

        return new VoucherStatsDTO(
                (long) allVouchers.size(),
                (long) validVouchers.size(),
                (long) expiredVouchers.size(),
                validVouchers,
                expiredVouchers,
                search
        );
    }

    @Override
    public List<String> getValidCourseTitlesForVoucher(Long voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        if (voucher.getVoucherCourses() == null || voucher.getVoucherCourses().isEmpty()) {
            return new ArrayList<>();
        }
        return voucher.getVoucherCourses().stream()
                .map(vc -> vc.getCourse().getTitle())
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> applyVoucherToCourse(Long userId, Long courseId, Long voucherId) {
        Map<String, Object> result = new HashMap<>();
        if (!userRepository.existsById(userId)) {
            result.put("error", "User not found");
            return result;
        }
        if (!courseRepository.existsById(courseId)) {
            result.put("error", "Course not found");
            return result;
        }
        if (!voucherRepository.existsById(voucherId)) {
            result.put("error", "Voucher not found");
            return result;
        }
        var userVoucherOpt = userVoucherRepository.findByUser_UserIdAndVoucher_VoucherId(userId, voucherId);
        if (userVoucherOpt.isEmpty()) {
            result.put("error", "User does not have this voucher");
            return result;
        }
        var userVoucher = userVoucherOpt.get();
        if (Boolean.TRUE.equals(userVoucher.getIsUsed())) {
            result.put("error", "Voucher has already been used");
            return result;
        }
        double originalPrice = courseRepository.findById(courseId)
                .map(course -> course.getPrice().doubleValue())
                .orElse(0.0);
        double discount = voucherRepository.findById(voucherId)
                .map(voucher -> voucher.getDiscount())
                .orElse(0.0);
        String voucherCode = voucherRepository.findById(voucherId)
                .map(voucher -> voucher.getCode())
                .orElse("");
        double discountedPrice = discount >= 100.0 ? 0 : Math.round(originalPrice * (1 - discount / 100.0));
        result.put("voucherId", voucherId);
        result.put("voucherCode", voucherCode);
        result.put("discountedPrice", (long) discountedPrice);
        return result;
    }
    @Override
    @Transactional
    public VoucherDTO createVoucherForInstructor(VoucherDTO voucherDTO, Long instructorId, List<Long> selectedCourses) {

        if (voucherRepository.findByCode(voucherDTO.getCode().trim()).isPresent()) {
            throw new IllegalArgumentException("Voucher code already exists!");
        }

        if (Boolean.FALSE.equals(voucherDTO.getIsGlobal()) && (selectedCourses == null || selectedCourses.isEmpty())) {
            throw new IllegalArgumentException("Please select at least one course!");
        }

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor does not exist!"));

        Voucher voucher = new Voucher();
        voucher.setCode(voucherDTO.getCode().trim());
        voucher.setDiscount(voucherDTO.getDiscount());

        LocalDate expiryDate = voucherDTO.getExpiryDate();
        if (expiryDate != null) {
            voucher.setExpiryDate(expiryDate);
        } else {
            voucher.setExpiryDate(null);
        }

        voucher.setLimitation(voucherDTO.getLimitation());
        voucher.setUsedCount(0L);
        voucher.setIsActive(true);
        voucher.setIsGlobal(voucherDTO.getIsGlobal());
        voucher.setIsPublic(voucherDTO.getIsPublic());
        voucher.setCreatedDate(LocalDate.now());
        voucher.setInstructor(instructor);

        voucher = voucherRepository.save(voucher);

        List<Long> courseIdsToApply;
        if (Boolean.TRUE.equals(voucher.getIsGlobal())) {
            // Chỉ lấy các course đang publish
            courseIdsToApply = courseRepository.findByInstructorUserIdAndStatus(instructorId, "publish")
                    .stream().map(Course::getCourseId).toList();
        } else {
            courseIdsToApply = selectedCourses != null ? selectedCourses : List.of();
        }
        if (!courseIdsToApply.isEmpty()) {
            List<com.OLearning.entity.VoucherCourse> voucherCourses = new ArrayList<>();
            for (Long courseId : courseIdsToApply) {
                Course course = courseRepository.findById(courseId).orElse(null);
                if (course != null && course.getInstructor().getUserId().equals(instructorId)) {
                    com.OLearning.entity.VoucherCourse vc = new com.OLearning.entity.VoucherCourse();
                    vc.setVoucher(voucher);
                    vc.setCourse(course);
                    voucherCourses.add(vc);
                }
            }
            voucherCourseRepository.saveAll(voucherCourses);
        }

        voucher = voucherRepository.findById(voucher.getVoucherId()).orElse(voucher);

        if (Boolean.FALSE.equals(voucher.getIsPublic())) {
            List<Enrollment> enrollments = enrollmentRepository.calculateSumEnrollment(instructorId);
            Set<Long> studentIds = new HashSet<>();
            for (Enrollment enrollment : enrollments) {
                if (enrollment.getUser() != null) {
                    studentIds.add(enrollment.getUser().getUserId());
                }
            }
            for (Long studentId : studentIds) {
                User student = userRepository.findById(studentId).orElse(null);
                if (student != null) {
                    Notification notification = new Notification();
                    notification.setUser(student);
                    notification.setMessage("You received a new voucher from the " + instructor.getFullName() + ". Voucher code: " + voucher.getCode());
                    notification.setType("VOUCHER_PRIVATE");
                    notification.setStatus("failed");
                    notification.setSentAt(java.time.LocalDateTime.now());
                    notificationService.sendMess(notification);
                }
            }
        }

        return voucherMapper.toVoucherDTO(voucher);
    }

    @Override
    public Map<String, Object> getFilteredVouchersForInstructor(Long instructorId, String keyword, String tabType, int page, int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Get all vouchers for instructor with search filter
            List<Voucher> allVouchers = voucherRepository.findAll().stream()
                    .filter(v -> v.getInstructor().getUserId().equals(instructorId))
                    .toList();
            // Apply search filter
            if (keyword != null && !keyword.isBlank()) {
                allVouchers = allVouchers.stream()
                        .filter(v -> v.getCode() != null && v.getCode().toLowerCase().contains(keyword.toLowerCase()))
                        .toList();
            }
            // Separate valid and expired vouchers
            LocalDate now = LocalDate.now();
            List<Voucher> validVouchers = allVouchers.stream()
                    .filter(v -> v.getIsActive() != null && v.getIsActive() && v.getExpiryDate() != null && v.getExpiryDate().isAfter(now))
                    .toList();
            List<Voucher> expiredVouchers = allVouchers.stream()
                    .filter(v -> v.getExpiryDate() == null || !v.getExpiryDate().isAfter(now) || Boolean.FALSE.equals(v.getIsActive()))
                    .toList();
            // Get vouchers for current tab with pagination
            List<Voucher> currentTabVouchers;
            if ("valid".equals(tabType)) {
                currentTabVouchers = validVouchers;
            } else {
                currentTabVouchers = expiredVouchers;
            }
            // Apply pagination
            int totalItems = currentTabVouchers.size();
            int totalPages = (int) Math.ceil((double) totalItems / size);
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, totalItems);
            List<Voucher> paginatedVouchers = currentTabVouchers.subList(startIndex, endIndex);
            List<VoucherDTO> voucherDTOs = paginatedVouchers.stream().map(voucherMapper::toVoucherDTO).toList();
            // Create stats
            VoucherStatsDTO stats = new VoucherStatsDTO(
                    (long) allVouchers.size(),
                    (long) validVouchers.size(),
                    (long) expiredVouchers.size(),
                    validVouchers,
                    expiredVouchers,
                    keyword
            );
            // Create pagination info
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("currentPage", page);
            pagination.put("totalPages", totalPages);
            pagination.put("totalItems", totalItems);
            pagination.put("hasNext", page < totalPages - 1);
            pagination.put("hasPrevious", page > 0);
            response.put("vouchers", voucherDTOs);
            response.put("stats", stats);
            response.put("pagination", pagination);
            response.put("success", true);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("success", false);
        }
        return response;
    }

    @Override
    public Page<VoucherDTO> getVouchersForInstructor(Long instructorId, String keyword, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("expiryDate").descending());
        Page<Voucher> voucherPage;
        LocalDate now = LocalDate.now();
        if (status != null && status.equals("valid")) {
            voucherPage = voucherRepository.findByInstructor_UserIdAndExpiryDateAfterAndIsActiveTrue(instructorId, now, pageable);
            if (keyword != null && !keyword.isBlank()) {
                voucherPage = new PageImpl<>(
                        voucherPage.getContent().stream()
                                .filter(v -> v.getCode() != null && v.getCode().toLowerCase().contains(keyword.toLowerCase()))
                                .toList(),
                        pageable,
                        voucherPage.getTotalElements()
                );
            }
        } else if (status != null && status.equals("expired")) {
            voucherPage = voucherRepository.findExpiredOrInactiveVouchersByInstructor(instructorId, now, pageable);
            // Lọc lại ở Java để lấy voucher expiryDate <= now hoặc isActive = false
            List<Voucher> filtered = voucherPage.getContent().stream()
                    .filter(v -> v.getExpiryDate() == null || !v.getExpiryDate().isAfter(now) || Boolean.FALSE.equals(v.getIsActive()))
                    .toList();
            voucherPage = new PageImpl<>(filtered, pageable, filtered.size());
            if (keyword != null && !keyword.isBlank()) {
                voucherPage = new PageImpl<>(
                        voucherPage.getContent().stream()
                                .filter(v -> v.getCode() != null && v.getCode().toLowerCase().contains(keyword.toLowerCase()))
                                .toList(),
                        pageable,
                        voucherPage.getTotalElements()
                );
            }
        } else {
            voucherPage = voucherRepository.findByInstructor_UserIdAndCodeContainingIgnoreCase(instructorId, keyword == null ? "" : keyword, pageable);
        }
        List<VoucherDTO> dtoList = voucherPage.getContent().stream().map(voucherMapper::toVoucherDTO).toList();
        return new PageImpl<>(dtoList, pageable, voucherPage.getTotalElements());
    }

    @Override
    public List<VoucherDTO> getPublicVouchers() {
        LocalDate now = LocalDate.now();
        List<Voucher> publicVouchers = voucherRepository.findAvailablePublicVouchers(now).stream()
                .collect(Collectors.toList());
        return publicVouchers.stream()
                .map(voucherMapper::toVoucherDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void claimPublicVoucher(Long voucherId, Long userId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        if (!Boolean.TRUE.equals(voucher.getIsPublic())) {
            throw new RuntimeException("This voucher is not public and cannot be claimed.");
        }
        if (!voucher.getIsActive() || voucher.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Voucher is invalid or expired");
        }
        if (voucher.getLimitation() != null && voucher.getUsedCount() >= voucher.getLimitation()) {
            throw new RuntimeException("Voucher usage limit reached.");
        }
        // Chặn nếu user đã mua hết các course mà voucher áp dụng
        List<Course> courses = voucher.getVoucherCourses() == null ? List.of() : voucher.getVoucherCourses().stream().map(vc -> vc.getCourse()).toList();
        boolean allPurchased = courses.stream().allMatch(course -> enrollmentRepository.existsByUser_UserIdAndCourse_CourseId(userId, course.getCourseId()));
        if (!courses.isEmpty() && allPurchased) {
            throw new RuntimeException("You have already purchased all courses for this voucher. Cannot claim or enter this voucher.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userVoucherRepository.findByUser_UserIdAndVoucher_VoucherId(userId, voucherId).isPresent()) {
            throw new RuntimeException("You have already claimed this voucher.");
        }

        UserVoucher userVoucher = new UserVoucher();
        userVoucher.setUser(user);
        userVoucher.setVoucher(voucher);
        userVoucher.setIsUsed(false);
        userVoucherRepository.save(userVoucher);

        voucher.setUsedCount(voucher.getUsedCount() + 1);
        voucherRepository.save(voucher);
    }
}