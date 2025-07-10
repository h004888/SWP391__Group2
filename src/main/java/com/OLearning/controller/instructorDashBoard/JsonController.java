package com.OLearning.controller.instructorDashBoard;

import com.OLearning.repository.CourseRepository;
import com.OLearning.service.cloudinary.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.OLearning.repository.VideoRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.entity.User;
import com.OLearning.entity.Video;
import com.OLearning.entity.Lesson;
import com.OLearning.entity.Chapter;
import com.OLearning.entity.Course;
import com.OLearning.entity.Enrollment;

import java.util.Optional;

@RequestMapping("/api/video")
@RestController
public class JsonController {
    @Autowired
    private UploadFile uploadFile;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;

//    @GetMapping("/signed-url")
//    public ResponseEntity<String> getSignedUrl(
//            @RequestParam String publicId,
//            @RequestParam(defaultValue = "video") String resourceType) {
//        String url = uploadFile.generateSignedVideoUrl(publicId, resourceType);
//        return ResponseEntity.ok(url);
//    }

    @GetMapping("/stream/video/{publicId}")
    public void streamVideo(@PathVariable String publicId, HttpServletRequest request, HttpServletResponse response) throws IOException {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
           response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
           return;
       }
       String username = null;
       Object principal = authentication.getPrincipal();
       if (principal instanceof UserDetails) {
           username = ((UserDetails) principal).getUsername();
       } else if (principal instanceof String) {
           username = (String) principal;
       }
       if (!checkUserAccessToVideo(username, publicId)) {
           response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
           return;
       }
        String cloudinaryUrl = uploadFile.generateSignedVideoUrl(publicId,"video");

        URL url = new URL(cloudinaryUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        String range = request.getHeader("Range");
        if (range != null) {
            conn.setRequestProperty("Range", range);
        }

        int code = conn.getResponseCode();
        response.setStatus(code);

        String contentType = conn.getHeaderField("Content-Type");
        if (contentType != null) response.setContentType(contentType);
        else response.setContentType("video/mp4");

        String contentRange = conn.getHeaderField("Content-Range");
        if (contentRange != null) response.setHeader("Content-Range", contentRange);

        String contentLength = conn.getHeaderField("Content-Length");
        if (contentLength != null) response.setHeader("Content-Length", contentLength);

        response.setHeader("Accept-Ranges", "bytes");

        if (code >= 400) {
            System.err.println("Cloudinary error: " + code + " - " + conn.getResponseMessage());
            response.sendError(code, conn.getResponseMessage());
            return;
        }

        try (java.io.InputStream is = conn.getInputStream();
             java.io.OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
    }

    private boolean checkUserAccessToVideo(String username, String videoUrl) {
        Optional<User> userOpt = userRepository.findByEmail(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        if (user.getRole() == null || user.getRole().getName() == null) {
            return false;
        }
        String roleName = user.getRole().getName();
        if ("admin".equalsIgnoreCase(roleName.toLowerCase()) || "Instructor".equalsIgnoreCase(roleName.toLowerCase())) {
            return true;
        }
        Optional<Video> videoOpt = videoRepository.findByVideoUrl(videoUrl);
        if (videoOpt.isEmpty()) {
            return false;
        }
        Video video = videoOpt.get();
        Lesson lesson = video.getLesson();
        if (lesson == null) {
            return false;
        }
        Chapter chapter = lesson.getChapter();
        if (chapter == null) {
            return false;
        }
        Course course = chapter.getCourse();
        if (course == null) {
            return false;
        }
        Long courseId = course.getCourseId();
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(user.getUserId(), courseId);
        boolean enrolled = enrollment != null;
        return enrolled;
    }
}
