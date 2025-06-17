package com.OLearning.service.comment.impl;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.User;
import com.OLearning.mapper.comment.CommentMapper;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CourseReviewRepository reviewRepo;
    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final CommentMapper mapper;

    @Override
    public void postComment(CommentDTO dto) {
        User user = userRepo.findById(dto.getUserId()).orElseThrow(() ->
                new RuntimeException("User not found"));
        Course course = courseRepo.findById(dto.getCourseId()).orElseThrow(() ->
                new RuntimeException("Course not found"));

        CourseReview review = mapper.toEntity(dto, user, course);
        reviewRepo.save(review);
    }
}