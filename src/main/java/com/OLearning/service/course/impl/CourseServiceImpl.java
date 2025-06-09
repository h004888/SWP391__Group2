package com.OLearning.service.course.impl;

import com.OLearning.dto.course.CourseAddDTO;
import com.OLearning.dto.course.CourseDTO;
import com.OLearning.entity.Categories;
import com.OLearning.entity.Course;
import com.OLearning.entity.User;
import com.OLearning.mapper.course.CourseMapper;
import com.OLearning.repository.InstructorCategoryRepo;
import com.OLearning.repository.InstructorCourseRepo;
import com.OLearning.repository.InstructorBuyPackagesRepository;
import com.OLearning.repository.InstructorUserRepo;
import com.OLearning.service.course.CourseService;
import com.OLearning.service.FileHelper.FileHelper;
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

@Service
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
    @Override
    public boolean canCreateCourse(Long userId) {
        List<Object[]> result = buyPackageRepository.findValidPackagesByUserId(userId);
        return !result.isEmpty();
        //if emty => false
        //else true
    }


    @Override
    public List<CourseDTO> findCourseByUserId(Long userId) {
        List<Course> listCourseRepo = instructorCourseRepo.findByInstructorUserId(userId);
        //da tim duoc ra danh sach theo user roi, gio can do ra dto de inra man hinh thoi
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

            if (course.getCategory() != null) {
                courseDTO.setCategoryName(course.getCategory().getName());
            } else {
                courseDTO.setCategoryName("Không rõ");
            }

            courseDTOList.add(courseDTO);
        }
        return courseDTOList;
    }


    //create new course service
    @Override
    public void createCourse(CourseAddDTO courseAddDTO, MultipartFile courseImg) {
        courseAddDTO.setUserId(2L);
        Course course = courseMapper.MapCourseAdd(courseAddDTO);
        Categories category = instructorCategoryRepo.findByName(courseAddDTO.getCategoryName());
        if (category == null) {
            throw new RuntimeException("not found: " + courseAddDTO.getCategoryName());
        }
        User instructor = instructorUserRepo.findById(courseAddDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("not found user: " + courseAddDTO.getUserId()));


        MultipartFile imageFile = courseAddDTO.getCourseImg();
        if(imageFile != null && !imageFile.isEmpty()) {
            try{
                File imageFoldder = new File(new ClassPathResource(".").getFile().getPath() + "/static/img");
                if(!imageFoldder.exists()){
                    imageFoldder.mkdirs();
                }
                String fileName = FileHelper.generateFileName(imageFile.getOriginalFilename());
                Path path = Paths.get(imageFoldder.getAbsolutePath() + File.separator + fileName);

                Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                course.setCourseImg(fileName);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        course.setInstructor(instructor);
        course.setCategory(category);
        instructorCourseRepo.save(course);
    }
}
