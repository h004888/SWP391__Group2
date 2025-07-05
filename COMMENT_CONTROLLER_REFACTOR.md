# Comment Controller Refactor

## Tổng quan
Đã gộp tất cả chức năng comment và report vào một CommentController duy nhất trong package `homePage`, loại bỏ các controller riêng biệt để cải thiện tổ chức code.

## Thay đổi chính

### 1. CommentController (src/main/java/com/OLearning/controller/homePage/CommentController.java)
- **Chức năng**: Xử lý tất cả các thao tác liên quan đến comment và report
- **Endpoints**:
  - `POST /api/course/{courseId}/comment` - Thêm comment mới
  - `POST /api/course/{courseId}/comment/reply` - Trả lời comment
  - `POST /api/course/{courseId}/comment/instructor-reply` - Instructor trả lời từ notification
  - `PUT /api/course/{courseId}/comment/{commentId}/edit` - Sửa comment
  - `PUT /api/course/{courseId}/comment/{commentId}/update` - Alias cho edit (backward compatibility)
  - `DELETE /api/course/{courseId}/comment/{commentId}/delete` - Xóa comment
  - `POST /api/course/{courseId}/comment/{commentId}/report` - Báo cáo comment
  - `POST /api/course/{courseId}/report` - Báo cáo khóa học
  - `GET /api/course/{courseId}/comments` - Lấy danh sách comments

### 2. CommentService (src/main/java/com/OLearning/service/comment/CommentService.java)
- Thêm method `reportComment(Long commentId, Long userId, Long courseId, String reason)`

### 3. CommentServiceImpl (src/main/java/com/OLearning/service/comment/impl/CommentServiceImpl.java)
- Implement method `reportComment()` để xử lý báo cáo comment
- Sử dụng Report entity để lưu trữ báo cáo

### 4. UserCourseController (src/main/java/com/OLearning/controller/homePage/UserCourseController.java)
- Loại bỏ dependency injection của CommentController để tránh circular dependency
- Load comments trực tiếp từ repository thay vì gọi API
- Đơn giản hóa logic và cải thiện performance

## Controllers đã xóa
- `src/main/java/com/OLearning/controller/api/CommentController.java`
- `src/main/java/com/OLearning/controller/api/ReportController.java`
- `src/main/java/com/OLearning/controller/api/CommentLoadController.java`
- `src/main/java/com/OLearning/controller/homePage/CommentLoadController.java`
- `src/main/java/com/OLearning/controller/home/CourseDetailMinController.java`

## Lợi ích
1. **Tổ chức code tốt hơn**: Tất cả chức năng comment/report trong một controller
2. **Dễ bảo trì**: Không cần quản lý nhiều controller riêng biệt
3. **Backward compatibility**: Giữ nguyên các API endpoints để frontend không cần thay đổi
4. **Separation of concerns**: CommentController chỉ xử lý comment/report, UserCourseController xử lý navigation
5. **Tránh circular dependency**: UserCourseController load comments trực tiếp thay vì inject CommentController

## Template compatibility
- Template `mainContentLessonVideo.html` sử dụng các API endpoints từ CommentController
- Tất cả JavaScript functions (add, reply, edit, delete, report) đều hoạt động với endpoints mới
- Không cần thay đổi frontend code

## Testing
- Compile thành công không có lỗi
- Tất cả endpoints được định nghĩa đúng
- Fallback mechanism đảm bảo tính ổn định 