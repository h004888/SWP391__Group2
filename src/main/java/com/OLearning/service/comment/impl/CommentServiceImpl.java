package com.OLearning.service.comment.impl;

import com.OLearning.dto.comment.CommentDTO;
import com.OLearning.entity.Course;
import com.OLearning.entity.CourseReview;
import com.OLearning.entity.Enrollment;
import com.OLearning.entity.Report;
import com.OLearning.entity.User;
import com.OLearning.entity.Lesson;
import com.OLearning.entity.Notification;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.repository.ReportRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.repository.LessonRepository;
import com.OLearning.service.comment.CommentService;
import com.OLearning.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CourseRepository courseRepo;
    private final UserRepository userRepo;
    private final CourseReviewRepository reviewRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final ReportRepository reportRepo;
    private final LessonRepository lessonRepo;
    private final NotificationService notificationService;

    @Override
    public CourseReview addComment(CommentDTO dto, Long userId, Long courseId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));

        // Kiểm tra xem user đã đăng ký khóa học chưa
        Enrollment enrollment = enrollmentRepo.findFirstByUserAndCourseOrderByEnrollmentDateDesc(user, course)
                .orElseThrow(() -> new RuntimeException("Bạn cần đăng ký khóa học để có thể bình luận"));

        // LessonId là bắt buộc cho comment
        if (dto.getLessonId() == null) {
            throw new RuntimeException("Comment phải thuộc về một bài học cụ thể");
        }
        
        Lesson lesson = lessonRepo.findById(dto.getLessonId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học"));
        
        // Verify lesson belongs to the course
        if (!lesson.getChapter().getCourse().getCourseId().equals(courseId)) {
            throw new RuntimeException("Bài học không thuộc khóa học này");
        }

        CourseReview review = new CourseReview();
        review.setEnrollment(enrollment);
        review.setCourse(course);
        review.setLesson(lesson);
        review.setComment(dto.getComment());
        review.setRating(null); // Set rating = null for comments
        review.setCreatedAt(LocalDateTime.now());
        reviewRepo.save(review);
        
        System.out.println("Comment added successfully: " + review.getReviewId() + " for lesson: " + lesson.getLessonId());
        return review;
    }

    @Override
    public void replyComment(CommentDTO dto, Long userId, Long courseId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));

        // Nếu là instructor của khóa học thì không cần enrollment
        Enrollment enrollment = null;
        if (course.getInstructor() != null && course.getInstructor().getUserId().equals(userId)) {
            enrollment = null; // Instructor trả lời, không cần enrollment
        } else {
            enrollment = enrollmentRepo.findFirstByUserAndCourseOrderByEnrollmentDateDesc(user, course)
                    .orElseThrow(() -> new RuntimeException("Bạn cần đăng ký khóa học để có thể trả lời bình luận"));
        }

        // Kiểm tra comment cha tồn tại
        CourseReview parentReview = reviewRepo.findById(dto.getParentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận gốc"));

        // Get lesson from parent comment
        Lesson lesson = parentReview.getLesson();

        CourseReview reply = new CourseReview();
        reply.setEnrollment(enrollment);
        reply.setCourse(course);
        reply.setLesson(lesson); // Reply sẽ có cùng lesson với parent comment
        reply.setComment(dto.getComment());
        reply.setRating(null); // Set rating = null for replies
        reply.setCreatedAt(LocalDateTime.now());
        reply.setParentReview(parentReview);
        reply.setReplyToUserName(dto.getReplyToUserName());
        reviewRepo.save(reply);
        
        System.out.println("Reply added successfully: " + reply.getReviewId() + " for lesson: " + (lesson != null ? lesson.getLessonId() : "course-level"));
        
        // Send notification to the original commenter if not replying to self
        User parentUser = parentReview.getEnrollment().getUser();
        if (!parentUser.getUserId().equals(userId)) {
            Notification notification = new Notification();
            notification.setType("COMMENT");
            notification.setUser(parentUser);
            notification.setCourse(course);
            notification.setCommentId(parentReview.getReviewId());
            notification.setMessage("Bình luận của bạn đã nhận được một phản hồi mới.");
            notification.setSentAt(LocalDateTime.now());
            notification.setStatus("failed");
            notificationService.sendMess(notification);
        }
    }

    @Override
    public void editComment(CommentDTO dto, Long userId, Long courseId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));

        // Kiểm tra comment tồn tại và thuộc về user
        CourseReview review = reviewRepo.findById(dto.getReviewId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

        if (!review.getEnrollment().getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa bình luận này");
        }

        review.setComment(dto.getComment());
        review.setUpdatedAt(LocalDateTime.now());
        reviewRepo.save(review);
    }

    @Override
    public void deleteComment(Long reviewId, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Kiểm tra comment tồn tại và thuộc về user
        CourseReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

        if (!review.getEnrollment().getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa bình luận này");
        }

        // Delete all replies first
        List<CourseReview> replies = reviewRepo.findByParentReviewOrderByCreatedAtDesc(review);
        for (CourseReview reply : replies) {
            reviewRepo.delete(reply);
        }

        reviewRepo.delete(review);
    }

    @Override
    public void reportComment(Long reviewId, Long userId, String reason) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        CourseReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));

        // Check if user is not reporting their own comment
        Long ownerId = null;
        if (review.getEnrollment() != null && review.getEnrollment().getUser() != null) {
            ownerId = review.getEnrollment().getUser().getUserId();
        } else if (review.getCourse() != null && review.getCourse().getInstructor() != null) {
            ownerId = review.getCourse().getInstructor().getUserId();
        }
        if (ownerId != null && ownerId.equals(userId)) {
            throw new RuntimeException("Bạn không thể báo cáo bình luận của chính mình");
        }

        Report report = new Report();
        report.setReportType("COMMENT");
        report.setUser(user);
        report.setCourse(review.getCourse());
        report.setContent(reason);
        report.setCreatedAt(LocalDateTime.now());
        report.setStatus("pending");
        report.setCommentId(reviewId);
        reportRepo.save(report);
    }

    @Override
    public void setCommentHidden(Long reviewId, boolean hidden) {
        CourseReview review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận"));
        review.setHidden(hidden);
        reviewRepo.save(review);
        // Gửi notification cho user khi comment bị ẩn
        if (hidden) {
            User commentUser = review.getEnrollment().getUser();
            Notification notification = new Notification();
            notification.setType("COMMENT_HIDDEN");
            notification.setUser(commentUser);
            notification.setCourse(review.getCourse());
            notification.setCommentId(review.getReviewId());
            notification.setMessage("Bình luận của bạn đã bị ẩn bởi quản trị viên.");
            notification.setSentAt(LocalDateTime.now());
            notification.setStatus("failed");
            notificationService.sendMess(notification);
        }
    }
}


