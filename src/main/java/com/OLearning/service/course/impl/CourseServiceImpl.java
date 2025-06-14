package com.OLearning.service.course.impl;

import com.OLearning.dto.course.AddCourseStep1DTO;
import com.OLearning.dto.course.AddCourseStep3DTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.dto.course.CourseMediaDTO;
import com.OLearning.entity.Category;
import com.OLearning.entity.Course;
import com.OLearning.entity.User;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.repository.*;
import com.OLearning.service.cloudinary.UploadFile;
import com.OLearning.service.course.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private UploadFile uploadFile;
    @Autowired
    private InstructorCourseRepo instructorCourseRepo;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private InstructorCategoryRepo instructorCategoryRepo;
    @Autowired
    private InstructorUserRepo instructorUserRepo;

    @Override
    public List<CourseDTO> findCourseByUserId(Long userId) {
        List<Course> listCourseRepo = instructorCourseRepo.findByInstructorUserId(userId);
        List<CourseDTO> courseDTOList = new ArrayList<>();
        for (Course course : listCourseRepo) {
            CourseDTO courseDTO = courseMapper.MapCourseDTO(course);
            if (course.getCategory() != null) {
                courseDTO.setCategoryName(course.getCategory().getName());
            } else {
                courseDTO.setCategoryName("not found");
            }
            courseDTOList.add(courseDTO);
        }
        return courseDTOList;
    }
    //courses phan trang
    @Override
    public Page<CourseDTO> findCourseByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = instructorCourseRepo.findByInstructorUserId(userId, pageable);//Page<Course> la doi tuong chua ca danh sach khoa hoc
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
    public void deleteCourse(Long courseId) {
        instructorCourseRepo.deleteById(courseId);
    }

    @Override
    public Course findCourseById(Long courseId) {
        Optional<Course> course = instructorCourseRepo.findById(courseId);
        if (course.isPresent()) {
            return course.get();
        }
        return null;
    }

    @Override
    public Course createCourseStep1(Long courseId, AddCourseStep1DTO addCourseStep1DTO) {
        Course course = (courseId == null) ? new Course() : findCourseById(courseId);
        courseMapper.CourseBasic(addCourseStep1DTO, course);
        Category category = instructorCategoryRepo.findByName(addCourseStep1DTO.getCategoryName());
        if (category == null) {
            throw new RuntimeException("not found: " + addCourseStep1DTO.getCategoryName());
        }
        addCourseStep1DTO.setUserId(2L);
        User instructor = instructorUserRepo.findById(addCourseStep1DTO.getUserId()).orElseThrow();
        course.setInstructor(instructor);
        course.setCategory(category);
        return instructorCourseRepo.save(course);
    }
    @Autowired
    ChapterRepository chapterRepo;
    @Autowired
    LessonRepository lessonRepo;


    @Override
    public Course submitCourse(Long courseId, String status) {
        Course course = (courseId == null) ? new Course() : findCourseById(courseId);
        course.setStatus(status);
        return instructorCourseRepo.save(course);
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
        
        return instructorCourseRepo.save(course);
    } catch (IOException e) {
        throw new RuntimeException("Failed to upload course media", e);
    }
    }

    @Override
    public void saveCourse(Long courseId) {
        Course course = (courseId == null) ? new Course() : findCourseById(courseId);
        instructorCourseRepo.save(course);
    }
}
