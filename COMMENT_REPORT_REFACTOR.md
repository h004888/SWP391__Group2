# Comment và Report Controller Refactor

## Tổng quan
Đã tách riêng các chức năng comment và report thành các controller riêng biệt trong package `homePage` và xóa hoàn toàn `CourseDetailMinController`.

## Cấu trúc mới

### 1. CommentController (`src/main/java/com/OLearning/controller/homePage/CommentController.java`)
**Chức năng:** Xử lý tất cả các thao tác CRUD cho comments

**Endpoints:**
- `POST /api/course/{courseId}/comment` - Thêm comment mới
- `POST /api/course/{courseId}/comment/reply` - Trả lời comment
- `POST /api/course/{courseId}/comment/instructor-reply` - Instructor trả lời comment
- `PUT /api/course/{courseId}/comment/{commentId}/edit` - Sửa comment
- `DELETE /api/course/{courseId}/comment/{commentId}/delete` - Xóa comment

### 2. ReportController (`src/main/java/com/OLearning/controller/homePage/ReportController.java`)
**Chức năng:** Xử lý tất cả các thao tác báo cáo

**Endpoints:**
- `POST /api/course/{courseId}/comment/{commentId}/report` - Báo cáo comment
- `POST /api/course/{courseId}/report` - Báo cáo khóa học

### 3. CommentLoadController (`src/main/java/com/OLearning/controller/homePage/CommentLoadController.java`)
**Chức năng:** Load comments cho khóa học

**Endpoints:**
- `GET /api/course/{courseId}/comments` - Lấy danh sách comments của khóa học

### 4. UserCourseController (Đã được mở rộng)
**Chức năng:** Xử lý tất cả các trang liên quan đến khóa học và học tập

**Endpoints:**
- `GET /learning` - Dashboard người dùng
- `GET /learning/course/{courseId}/detail` - Trang chi tiết khóa học (cho người đã đăng ký)
- `GET /learning/course/{courseId}/public` - Trang chi tiết khóa học công khai (cho mọi người)
- `GET /learning/course/{courseId}/lesson/{lessonId}` - Trang học bài cụ thể

### 5. CourseDetailMinController (ĐÃ XÓA)
**Trạng thái:** Đã xóa hoàn toàn
**Lý do:** Chức năng đã được tích hợp vào UserCourseController

## Lợi ích của việc refactor

1. **Separation of Concerns:** Mỗi controller chỉ xử lý một chức năng cụ thể
2. **Dễ bảo trì:** Code được tổ chức rõ ràng, dễ tìm và sửa
3. **Dễ test:** Có thể test từng controller riêng biệt
4. **Dễ mở rộng:** Có thể thêm chức năng mới mà không ảnh hưởng đến các controller khác
5. **API Documentation:** Các endpoint được nhóm logic rõ ràng
6. **Giảm code trùng lặp:** Loại bỏ CourseDetailMinController giúp giảm duplicate code

## Sử dụng

### Frontend JavaScript
Các API endpoint vẫn giữ nguyên URL pattern, nên không cần thay đổi JavaScript code:

```javascript
// Thêm comment
fetch(`/api/course/${courseId}/comment`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ comment: commentText })
});

// Báo cáo comment
fetch(`/api/course/${courseId}/comment/${commentId}/report`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ reason: reportReason })
});
```

### Backend Integration
Các service và repository vẫn được sử dụng như cũ, chỉ thay đổi cách tổ chức controller.

## Migration Notes

- Tất cả API endpoints vẫn giữ nguyên URL pattern
- Không cần thay đổi frontend code
- Không cần thay đổi service layer
- Chỉ thay đổi cách tổ chức controller layer
- CourseDetailMinController đã được xóa hoàn toàn
- Các controller đã được di chuyển từ package `api` sang package `homePage`

## Cấu trúc thư mục mới

```
src/main/java/com/OLearning/controller/homePage/
├── UserCourseController.java (Main course pages)
├── CommentController.java (Comment CRUD operations)
├── ReportController.java (Report operations)
└── CommentLoadController.java (Load comments)
```

## URL Mapping

### Course Pages:
- `/learning/course/{courseId}/detail` - Course detail (enrolled users)
- `/learning/course/{courseId}/public` - Course detail (public)
- `/learning/course/{courseId}/lesson/{lessonId}` - Lesson page

### API Endpoints:
- `/api/course/{courseId}/comment` - Comment operations
- `/api/course/{courseId}/comment/{commentId}/report` - Report comment
- `/api/course/{courseId}/report` - Report course
- `/api/course/{courseId}/comments` - Load comments 