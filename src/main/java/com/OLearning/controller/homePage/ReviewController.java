package com.OLearning.controller.homePage;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.User;
import com.OLearning.mapper.comment.CommentMapper;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class ReviewController {

    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final CourseReviewRepository reviewRepo;
    private final CommentMapper commentMapper;

    @GetMapping("/{courseId}/reviews")
    @ResponseBody
    public ResponseEntity<?> getCourseReviews(@PathVariable Long courseId) {
        try {
            Course course = courseRepo.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));
            

            List<CourseReview> parentReviews = reviewRepo.findReviewsByCourseAndLessonOrNullAndParentReviewIsNull(course, null);
            List<CommentDTO> reviews = new ArrayList<>();
            
            for (CourseReview parentReview : parentReviews) {
                CommentDTO reviewDTO = commentMapper.toDTO(parentReview);
                

                List<CourseReview> replies = reviewRepo.findByParentReviewOrderByCreatedAtDesc(parentReview);
                List<CommentDTO> replyDTOs = replies.stream()
                        .map(reply -> commentMapper.toDTO(reply))
                        .collect(Collectors.toList());
                reviewDTO.setChildren(replyDTOs);
                
                reviews.add(reviewDTO);
            }
            
            return ResponseEntity.ok(reviews);
        } catch (RuntimeException e) {
            System.err.println("Error getting course reviews: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Lỗi khi lấy đánh giá: " + e.getMessage()));
        }
    }


}
