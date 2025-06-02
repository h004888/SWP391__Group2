package com.OLearning.service.instructorDashboard.impl;

import com.OLearning.dto.instructorDashboard.AddCourseStep1DTO;
import com.OLearning.dto.instructorDashboard.CourseAddDTO;
import com.OLearning.dto.instructorDashboard.CourseDTO;
import com.OLearning.entity.Categories;
import com.OLearning.entity.Course;
import com.OLearning.entity.User;
import com.OLearning.mapper.instructorDashBoard.CourseMapper;
import com.OLearning.repository.instructorDashBoard.InstructorCategoryRepo;
import com.OLearning.repository.instructorDashBoard.InstructorCourseRepo;
import com.OLearning.repository.instructorDashBoard.InstructorBuyPackagesRepository;
import com.OLearning.repository.instructorDashBoard.InstructorUserRepo;
import com.OLearning.service.instructorDashboard.CourseService;
import com.OLearning.service.instructorDashboard.FileHelper.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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

@Service("instructorCourseService")
public class CourseServiceImpl implements CourseService {

    @Autowired
    private InstructorCourseRepo instructorCourseRepo;
    @Autowired
    private InstructorBuyPackagesRepository buyPackageRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private InstructorCategoryRepo instructorCategoryRepo;
    @Autowired
    private InstructorUserRepo instructorUserRepo;
//    @Override
//    public boolean canCreateCourse(Long userId) {
//        List<Long> result = buyPackageRepository.findValidPackagesByUserId(userId);
//        return !result.isEmpty();
//        //if emty => false
//        //else true
//    }


    @Override
    public List<CourseDTO> findCourseByUserId(Long userId) {
        List<Course> listCourseRepo = instructorCourseRepo.findByInstructorUserId(userId);
        List<CourseDTO> courseDTOList = new ArrayList<>();
        for (Course course : listCourseRepo) {
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setCourseId(course.getCourseId());
            courseDTO.setCourseImg(course.getCourseImg());
            courseDTO.setTitle(course.getTitle());
            courseDTO.setCreatedAt(course.getCreatedAt());
            courseDTO.setPrice(course.getPrice());
            courseDTO.setDuration(course.getDuration());
            courseDTO.setDiscount(course.getDiscount());
            courseDTO.setTotalLessons(course.getTotalLessons());
            courseDTO.setStatus(course.getStatus());
            if (course.getCategory() != null) {
                courseDTO.setCategoryName(course.getCategory().getName());
            } else {
                courseDTO.setCategoryName("not found");
            }
            courseDTOList.add(courseDTO);
        }
        return courseDTOList;
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
        //no set xong may cai co ban roi

        Categories category = instructorCategoryRepo.findByName(addCourseStep1DTO.getCategoryName());
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
    public AddCourseStep1DTO draftCourseStep1(Course course) {
        AddCourseStep1DTO courseDTO = courseMapper.DraftStep1(course);
        return courseDTO;
    }
}
