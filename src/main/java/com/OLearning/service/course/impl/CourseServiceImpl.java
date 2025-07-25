package com.OLearning.service.course.impl;

import com.OLearning.dto.course.AddCourseStep1DTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseDetailDTO;
import com.OLearning.dto.course.CourseMediaDTO;
import com.OLearning.entity.*;
import com.OLearning.dto.course.CourseViewDTO;
import com.OLearning.mapper.course.CourseDetailMapper;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.repository.*;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.cloudinary.UploadFile;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.email.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private UploadFile uploadFile;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private CourseDetailMapper courseDetailMapper;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private EmailService emailService;

    @Override
    public Page<CourseDTO> getAllCourses(Pageable pageable) {
        Page<Course> courseList = courseRepository.findAll(pageable);
        return courseList.map(courseMapper::MapCourseDTO);
    }

    public Pageable getPageable(int page, int size, String sortBy) {
        // Chỉ cho phép sort trong DB với các field có trong DB thật
        if ("Newest".equals(sortBy) || "Free".equals(sortBy)) {
            return PageRequest.of(page, size, Sort.by("createdAt").descending());
        }
        // Với MostPopular & MostViewed: sort sau khi lấy dữ liệu
        return PageRequest.of(page, size); // Không sort trong DB
    }

    // courses phan trang
    @Override
    public Page<CourseDTO> findCourseByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = courseRepository.findByInstructorUserId(userId, pageable);// Page<Course> la doi tuong
                                                                                            // chua ca danh sach khoa
                                                                                            // hoc
        List<CourseDTO> courseDTOList = new ArrayList<>();
        for (Course course : coursePage.getContent()) {
            CourseDTO courseDTO = courseMapper.MapCourseDTO(course);
            if (course.getCategory() != null) {
                courseDTO.setCategoryName(course.getCategory().getName());
            } else {
                courseDTO.setCategoryName("not found");
            }
            courseDTOList.add(courseDTO);
        }
        return new PageImpl<>(courseDTOList, pageable, coursePage.getTotalElements());
    }

    @Override
    public Page<CourseDTO> searchCourse(Long userId, String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = courseRepository.searchCoursesByKeyWord(title, userId, pageable);
        List<CourseDTO> courseDTOList = new ArrayList<>();
        for (Course course : coursePage.getContent()) {
            CourseDTO courseDTO = courseMapper.MapCourseDTO(course);
            if (course.getCategory() != null) {
                courseDTO.setCategoryName(course.getCategory().getName());
            } else {
                courseDTO.setCategoryName("not found");
            }
            courseDTOList.add(courseDTO);
        }
        return new PageImpl<>(courseDTOList, pageable, coursePage.getTotalElements());
    }

    @Override
    public Page<CourseViewDTO> searchCoursesGrid(
            List<Long> categoryIds,
            List<String> priceFilters,
            List<String> levels,
            String sortBy,
            String keyword,
            int page,
            int size) {

        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        if (categoryIds != null && categoryIds.isEmpty()) {
            categoryIds = null;
        }

        if (priceFilters != null) {
            if (priceFilters.contains("All")) {
                priceFilters = null;
            }
        }

        if (levels != null) {
            if (levels.isEmpty()) {
                levels = null;
            }
        }

        Pageable pageable = getPageable(page, size, sortBy);

        Page<Course> coursesPage = courseRepository.searchCourses(
                keyword,
                categoryIds,
                priceFilters,
                levels,
                pageable);

        List<Course> result = new ArrayList<>(coursesPage.getContent());

        // Sort bằng Java nếu là MostPopular hoặc MostViewed
        if ("MostPopular".equals(sortBy)) {
            result.sort((a, b) -> Long.compare(b.getCourseReviews().size(), a.getCourseReviews().size()));
        } else if ("MostViewed".equals(sortBy)) {
            result.sort((a, b) -> Integer.compare(b.getEnrollments().size(), a.getEnrollments().size()));
        }

        // Trả về lại Page<CourseDTO>
        return new PageImpl<>(
                result,
                pageable,
                coursesPage.getTotalElements()).map(CourseMapper::toCourseViewDTO);
    }

    @Override
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }

    @Override
    public CourseViewDTO getCourseRecentIncomplete(Long userId) {
        return CourseMapper.toCourseViewDTO(courseRepository.findMostRecentIncompleteCourse(userId));
    }

    @Override
    public List<CourseViewDTO> getTopCourses() {
        return courseRepository.findAllPublishedOrderByStudentCountDesc().stream()
                .map(CourseMapper::toCourseViewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CourseDetailDTO> getDetailCourse(Long id) {
        return courseRepository.findById(id).map(courseDetailMapper::toDTO);
    }

    @Override
    public Course findCourseById(Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            return course.get();
        }
        return null;
    }

    @Override
    public CourseViewDTO getCourseById(Long id) {
        return courseRepository.findById(id)
                .map(CourseMapper::toCourseViewDTO)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + id));
    }

    @Override
    public List<Chapter> getChaptersWithLessons(Long courseId) {
        return chapterRepository.findByCourseIdWithLessons(courseId);

    }

    @Override
    public Course createCourseStep1(Long courseId, AddCourseStep1DTO addCourseStep1DTO) {
        Course course = (courseId == null) ? new Course() : findCourseById(courseId);
        courseMapper.CourseBasic(addCourseStep1DTO, course);
        Category category = categoryRepository.findByName(addCourseStep1DTO.getCategoryName());
        if (category == null) {
            throw new RuntimeException("not found: " + addCourseStep1DTO.getCategoryName());
        }
        // lay userId tu tai khoan dang nhap
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        addCourseStep1DTO.setUserId(userId);
        User instructor = userRepository.findById(addCourseStep1DTO.getUserId()).orElseThrow();
        course.setInstructor(instructor);
        course.setCategory(category);
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    @Override
    public Course submitCourse(Long courseId, String status) {
        Course course = (courseId == null) ? new Course() : findCourseById(courseId);
        course.setUpdatedAt(LocalDateTime.now());
        course.setStatus(status);
        return courseRepository.save(course);
    }

    @Override
    public Page<CourseDTO> filterCoursesWithPagination(String keyword, Long category, String price, String status,
            int page, int size) {
        String searchKeyword = keyword != null && !keyword.trim().isEmpty() ? keyword.trim() : null;

        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = courseRepository.filterCourses(
                searchKeyword, category, price, status, pageable);
        return coursePage.map(course -> mapCourseToDTO(course));
    }

    @Override
    public Page<CourseDTO> filterCoursesInstructorManage(Long userId, Long categoryId, String status, String price,
            String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = courseRepository.findCoursesByFilters(userId, categoryId, status, price, title,
                pageable);
        return coursePage.map(course -> mapCourseToDTO(course));
    }


    private CourseDTO mapCourseToDTO(Course course) {
        CourseDTO dto = courseMapper.MapCourseDTO(course);

        // Map category name
        if (course.getCategory() != null) {
            dto.setCategoryName(course.getCategory().getName());
        } else {
            dto.setCategoryName("Not Found");
        }

        // Map course level
        dto.setCourseLevel(course.getCourseLevel());

        // Map course image
        dto.setCourseImg(course.getCourseImg());

        // Map price
        dto.setPrice(course.getPrice());

        // Map status
        dto.setStatus(course.getStatus());

        // Đếm số lượng học viên enrollment
        Long enrollmentCount = courseRepository.countEnrollmentsByCourseId(course.getCourseId());
        dto.setTotalStudentEnrolled(enrollmentCount != null ? enrollmentCount.intValue() : 0);

        // Đếm số lượng lesson
        Long lessonCount = courseRepository.countLessonsByCourseId(course.getCourseId());
        dto.setTotalLessons(lessonCount != null ? lessonCount.intValue() : 0);

        // Map isFree
        dto.setIsFree(course.getPrice() != null && course.getPrice() == 0);

        // Map instructor
        dto.setInstructor(course.getInstructor());

        // Map timestamps
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());

        return dto;
    }

    @Override
    public AddCourseStep1DTO draftCourseStep1(Course course) {
        AddCourseStep1DTO courseDTO = courseMapper.DraftStep1(course);
        return courseDTO;
    }

    @Override
    public boolean approveCourse(Long id) {
        return courseRepository.findById(id)
                .map(course -> {
                    if (!"approved".equalsIgnoreCase(course.getStatus())) {
                        course.setStatus("approved");
                        course.setUpdatedAt(LocalDateTime.now());
                        courseRepository.save(course);
                        // Gửi notification cho instructor
                        if (course.getInstructor() != null) {
                            Notification notification = new Notification();
                            notification.setUser(course.getInstructor());
                            notification.setCourse(course);
                            notification.setMessage(
                                    "Khóa học '" + course.getTitle() + "' của bạn đã được admin phê duyệt.");
                            notification.setType("COURSE_APPROVED");
                            notification.setStatus("failed");
                            notification.setSentAt(LocalDateTime.now());
                            notificationRepository.save(notification);
                        }
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    @Override
    public boolean rejectCourse(Long id) {
        return courseRepository.findById(id)
                .map(course -> {
                    if (!"rejected".equalsIgnoreCase(course.getStatus())) {
                        course.setStatus("rejected");
                        course.setUpdatedAt(LocalDateTime.now());
                        courseRepository.save(course);
                        // Gửi notification cho instructor
                        if (course.getInstructor() != null) {
                            Notification notification = new Notification();
                            notification.setUser(course.getInstructor());
                            notification.setCourse(course);
                            notification
                                    .setMessage("Khóa học '" + course.getTitle() + "' của bạn đã bị admin từ chối.");
                            notification.setType("COURSE_REJECTED");
                            notification.setStatus("failed");
                            notification.setSentAt(LocalDateTime.now());
                            notificationRepository.save(notification);
                        }
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    @Override
    public Course createCourseMedia(Long courseId, CourseMediaDTO CourseMediaDTO) {
        Course course = (courseId == null) ? new Course() : findCourseById(courseId);
        try {
            if (CourseMediaDTO.getImage() != null && !CourseMediaDTO.getImage().isEmpty()) {
                String imageUrl = uploadFile.uploadImageFile(CourseMediaDTO.getImage());
                course.setCourseImg(imageUrl);
            }

            if (CourseMediaDTO.getVideo() != null && !CourseMediaDTO.getVideo().isEmpty()) {
                String videoUrl = uploadFile.uploadVideoFile(CourseMediaDTO.getVideo());
                course.setVideoUrlPreview(videoUrl);
            }
            course.setUpdatedAt(LocalDateTime.now());
            return courseRepository.save(course);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload course media", e);
        }
    }

    @Override
    public void saveCourse(Long courseId) {
        Course course = (courseId == null) ? new Course() : findCourseById(courseId);
        course.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(course);
    }

    @Override
    public void blockCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        if (!"pending_block".equalsIgnoreCase(course.getStatus())) {
            throw new IllegalStateException("Chỉ có thể block khóa học khi đang ở trạng thái 'pending_block'.");
        }
        course.setStatus("blocked");
        courseRepository.save(course);
        User instructor = course.getInstructor();
        if (instructor != null) {
            Notification notification = new Notification();
            notification.setUser(instructor);
            notification.setCourse(course);
            notification.setMessage("Your course '" + course.getTitle() + "' has been officially blocked by admin.");
            notification.setType("CONFIRM_BLOCK");
            notification.setStatus("failed");
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }


    @Override
    public void unblockCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        course.setStatus("publish");
        courseRepository.save(course);
    }

    @Override
    public List<CourseDTO> getCourseDTOsByCategoryId(Long categoryId) {
        List<Course> courses = courseRepository.findByCategoryId(categoryId);
        return courses.stream()
                .map(courseMapper::MapCourseDTO)
                .toList();
    }

    @Override
    public List<CourseViewDTO> getCoursesByCategoryId(Long categoryId) {
        return courseRepository.findByCategoryIdAndStatusIgnoreCase(categoryId, "publish").stream()
                .map(CourseMapper::toCourseViewDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CourseViewDTO> getCourseByUserId(Long userId) {
        return courseRepository.findCoursesByUserId(userId).stream()
                .map(CourseMapper::toCourseViewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Course findById(Long courseId) {
        return courseRepository.findById(courseId).orElseThrow();
    }

    @Override
    public int countByInstructorAndStatus(Long userId, String status) {
        return courseRepository.countByInstructorUserIdAndStatus(userId, status);
    }

    public int countByInstructorAndStatusWithFilter(Long userId, String status, Long categoryId, String price,
            String title) {
        return courseRepository.countByFilters(userId, categoryId, status, price, title);
    }


    public void setPendingBlock(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        course.setStatus("pending_block");
        courseRepository.save(course);
        User instructor = course.getInstructor();
        if (instructor != null) {
            com.OLearning.entity.Notification notification = new com.OLearning.entity.Notification();
            notification.setUser(instructor);
            notification.setCourse(course);
            notification.setMessage("Your course '" + course.getTitle()
                    + "' is pending block review by admin. Please check your email and respond if needed.");
            notification.setType("COURSE_BLOCKED");
            notification.setStatus("failed");
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            // Gửi email cho instructor
            String subject = "[OLearning] Your course is pending block review";
            String content = "Xin chào " + instructor.getFullName() + ",\n\n" +
                    "Khóa học '" + course.getTitle() + "' của bạn đang được xem xét để block bởi quản trị viên.\n" +
                    "Vui lòng đăng nhập vào hệ thống OLearning để phản hồi hoặc liên hệ bộ phận hỗ trợ nếu có thắc mắc.\n\n"
                    +
                    "Trân trọng,\nĐội ngũ OLearning";
            emailService.sendOTP(instructor.getEmail(), subject, content);
        }
    }
}