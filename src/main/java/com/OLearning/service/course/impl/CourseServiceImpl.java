package com.OLearning.service.course.impl;

import com.OLearning.dto.course.AddCourseStep1DTO;
import com.OLearning.dto.course.AddCourseStep3DTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.entity.*;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.repository.*;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.instructorDashBoard.FileHelper.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {

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
        courseMapper.Step1(addCourseStep1DTO, course);
        Category category = instructorCategoryRepo.findByName(addCourseStep1DTO.getCategoryName());
        if (category == null) {
            throw new RuntimeException("not found: " + addCourseStep1DTO.getCategoryName());
        }
        addCourseStep1DTO.setUserId(2L);
        User instructor = instructorUserRepo.findById(addCourseStep1DTO.getUserId()).orElseThrow();


        MultipartFile imageFile = addCourseStep1DTO.getCourseImg();
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                File imageFoldder = new File(new ClassPathResource(".").getFile().getPath() + "/static/img");
                if (!imageFoldder.exists()) {
                    imageFoldder.mkdirs();
                }
                String fileName = FileHelper.generateFileName(imageFile.getOriginalFilename());
                Path path = Paths.get(imageFoldder.getAbsolutePath() + File.separator + fileName);

                Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                course.setCourseImg(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        course.setInstructor(instructor);
        course.setCategory(category);
        return instructorCourseRepo.save(course);
    }
    @Override
    public Course createCourseStep1Demo(Long courseId, AddCourseStep1DTO addCourseStep1DTO) {
                return null;
    }
    @Autowired
    ChapterRepository chapterRepo;
    @Autowired
    LessonRepository lessonRepo;
    @Override
    public Course createCourseStep2(Long courseId) {
        Course course = (courseId == null) ? new Course() : findCourseById(courseId);
        courseMapper.Step2(course);
        int totalLessons = 0;
        int totalDuration = 0;
        List<Chapter> chapters = chapterRepo.findChaptersByCourse(courseId); //tim ra list chapter
        for (Chapter chapter : chapters) {
            List<Lesson> lessons = chapter.getLessons();
            totalLessons += lessons.size();
            for (Lesson lesson : lessons) {
                totalDuration += lesson.getDuration() != null ? lesson.getDuration() : 0; //can lam lai
            }
        }
        course.getListOfChapters().clear();
        course.getListOfChapters().addAll(chapters);
        course.setTotalLessons(totalLessons);
        course.setDuration(totalDuration);
        return instructorCourseRepo.save(course);
    }
    public Course createCourseStep3(Long courseId, AddCourseStep3DTO addCourseStep3DTO) {
        Course course = (courseId == null) ? new Course() : findCourseById(courseId);
        courseMapper.Step3(addCourseStep3DTO, course);
        course.setPrice(addCourseStep3DTO.getPrice());
        return instructorCourseRepo.save(course);
    }

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
}
