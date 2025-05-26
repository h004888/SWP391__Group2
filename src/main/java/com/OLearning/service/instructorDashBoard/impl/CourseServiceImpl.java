package com.OLearning.service.instructorDashBoard.impl;

import com.OLearning.dto.instructorDashboard.CourseDTO;
import com.OLearning.entity.Course;
import com.OLearning.repository.instructorDashBoard.InstructorCourseRepo;
import com.OLearning.repository.instructorDashBoard.InstructorPackageRepository;
import com.OLearning.service.instructorDashBoard.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private InstructorCourseRepo instructorCourseRepo;
        @Autowired
        private InstructorPackageRepository buyPackageRepository;

        public boolean canCreateCourse(Long userId) {
            List<Object[]> result = buyPackageRepository.findValidPackagesByUserId(userId);
            return !result.isEmpty();
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
                courseDTO.setCategoryName("Không rõ"); // hoặc để trống
            }

            courseDTOList.add(courseDTO);
        }
        return courseDTOList;
    }
}
