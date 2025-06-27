package com.OLearning.service.course.impl;

import com.OLearning.dto.course.AddCourseStep1DTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseDetailDTO;
import com.OLearning.dto.course.CourseMediaDTO;
import com.OLearning.entity.Category;
import com.OLearning.entity.Chapter;
import com.OLearning.entity.Course;
import com.OLearning.entity.User;
import com.OLearning.mapper.course.CourseDetailMapper;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.repository.*;
import com.OLearning.security.CustomUserDetails;
import com.OLearning.service.cloudinary.UploadFile;
import com.OLearning.service.course.CourseService;
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
    //courses phan trang
    @Override
    public Page<CourseDTO> findCourseByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = courseRepository.findByInstructorUserId(userId, pageable);//Page<Course> la doi tuong chua ca danh sach khoa hoc
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
    public Page<CourseDTO> searchCoursesGrid(
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

//        // Sort bằng Java nếu là MostPopular hoặc MostViewed
//        if ("MostPopular".equals(sortBy)) {
//            result.sort((a, b) -> Long.compare(b.getReviewCount(), a.getReviewCount()));
//        } else if ("MostViewed".equals(sortBy)) {
//            result.sort((a, b) -> Integer.compare(b.totalStudentEnrolled(), a.totalStudentEnrolled()));
//        }

        // Trả về lại Page<CourseDTO>
        return new PageImpl<>(
                result,
                pageable,
                coursesPage.getTotalElements()).map(CourseMapper::toDTO);
    }

    @Override
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);}

    @Override
    public List<Course> getTopCourses() {
        return courseRepository.findAllOrderByStudentCountDesc();
    }
    @Override
    public Optional<CourseDetailDTO> getDetailCourse(Long id) {
        return courseRepository.findById(id).map(courseDetailMapper::toDTO);
    }


    @Override
    public boolean approveCourse(Long id) {
        return courseRepository.findById(id)
                .map(course -> {
                    if (!"approved".equalsIgnoreCase(course.getStatus())) {
                        course.setStatus("approved");
                        course.setUpdatedAt(LocalDateTime.now());
                        courseRepository.save(course);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
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
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    @Override
    public List<Chapter> getChaptersWithLessons(Long courseId) {
        return chapterRepository.findByCourseIdWithLessons(courseId);

    }
    @Override
    public boolean rejectCourse(Long id) {
        return courseRepository.findById(id)
                .map(course -> {
                    if (!"rejected".equalsIgnoreCase(course.getStatus())) {
                        course.setStatus("rejected");
                        course.setUpdatedAt(LocalDateTime.now());
                        courseRepository.save(course);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    @Override
    public Course createCourseStep1(Long courseId, AddCourseStep1DTO addCourseStep1DTO) {
        Course course = (courseId == null) ? new Course() : findCourseById(courseId);
        courseMapper.CourseBasic(addCourseStep1DTO, course);
        Category category = categoryRepository.findByName(addCourseStep1DTO.getCategoryName());
        if (category == null) {
            throw new RuntimeException("not found: " + addCourseStep1DTO.getCategoryName());
        }
        //lay userId tu tai khoan dang nhap
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
    public Page<CourseDTO> filterCoursesWithPagination(String keyword, Long category, String price, String status, int page, int size) {
        String searchKeyword = keyword != null && !keyword.trim().isEmpty() ? keyword.trim() : null;

        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = courseRepository.filterCourses(
                searchKeyword, category, price, status, pageable
        );
        return coursePage.map(courseMapper::MapCourseDTO);
    }


    @Override
    public Page<CourseDTO> filterCoursesInstructorManage(Long userId, Long categoryId, String status, String price, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage;

        if (categoryId != null || (status != null && !status.isEmpty()) || (price != null && !price.isEmpty())) {
            coursePage = courseRepository.findCoursesByFilters(userId, categoryId, status, price, pageable);
        } else {
            coursePage = courseRepository.findByInstructorUserId(userId, pageable);
        }

        return coursePage.map(course -> {
            CourseDTO dto = courseMapper.MapCourseDTO(course);

            if (course.getCategory() != null) {
                dto.setCategoryName(course.getCategory().getName());
            }
            return dto;
        });
    }


    @Override
    public AddCourseStep1DTO draftCourseStep1(Course course) {
        AddCourseStep1DTO courseDTO = courseMapper.DraftStep1(course);
        return courseDTO;
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

}
