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
import java.util.Map;

@RequestMapping("/api/video")
@RestController
public class JsonController {
    @Autowired
    private UploadFile uploadFile;

//    @GetMapping("/signed-url")
//    public ResponseEntity<String> getSignedUrl(
//            @RequestParam String publicId,
//            @RequestParam(defaultValue = "video") String resourceType) {
//        String url = uploadFile.generateSignedVideoUrl(publicId, resourceType);
//        return ResponseEntity.ok(url);
//    }

    @GetMapping("/stream/video/{publicId}")
    public void streamVideo(@PathVariable String publicId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Lấy signed URL từ Cloudinary (type: private)
        String cloudinaryUrl = uploadFile.generateSignedVideoUrl(publicId,"video");

        java.net.URL url = new java.net.URL(cloudinaryUrl);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();

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
}
