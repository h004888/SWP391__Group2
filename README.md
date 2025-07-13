 # 🧠 OLearning Project - Spring Boot Web Application

> Hệ thống quản lý học tập dành cho quản trị viên, người dùng và nội dung học tập.

---

## 📁 Dự án bao gồm các module:

```
src/
 └─ main/
     ├─ java/com/OLearning/
     │   ├─ config/               → Cấu hình Spring
     │   ├─ controller/           → Xử lý request, chia theo module
     │   ├─ entity/               → Các lớp ánh xạ DB (JPA Entity)
     │   ├─ exception/            → Xử lý ngoại lệ tùy chỉnh
     │   ├─ repository/           → Giao tiếp DB (Spring Data JPA)
     │   ├─ service/              → Business logic
     │   └─ OLearningApplication.java
     └─ resources/
         ├─ static/               → Tài nguyên tĩnh (JS, CSS, ảnh)
         ├─ templates/
         │    └─ adminDashboard/
         │         ├─ fragments/  → Giao diện tái sử dụng (header, footer, v.v.)
         │         └─ pages/      → Các trang chính (Gợi ý chia riêng)
         └─ application.properties
```

---

## ✅ Code Convention (Quy tắc code thống nhất)

| Hạng mục | Quy định |
|---------|----------|
| **Tên class** | PascalCase: `CategoryController`, `UserServiceImpl` |
| **Tên biến** | camelCase: `categoryList`, `userId` |
| **Tên method** | camelCase, có động từ: `getAllUsers()`, `saveCategory()` |
| **Tên package** | Chữ thường, chia rõ theo vai trò: `controller.categories`, `repository.user` |
| **HTML file** | Tên tiếng Anh, snake-case hoặc camelCase: `createCategory.html`, `topbar.html` |
| **Tái sử dụng HTML** | Dùng `th:replace="fragments/header :: header"` để nhúng |
| **Comment code** | Rõ ràng, dùng `//` hoặc `/** */` nếu thật cần thiết |
| **Commit message** | Tiếng Anh ngắn gọn, theo format: `feat: add category creation`, `fix: resolve null pointer` |
| **Controller Mapping** | Dùng `@GetMapping`, `@PostMapping`, v.v. rõ ràng |
| **SQL Script** | Đặt trong `resources/db/`, không để chung với file Java |

---

## 🧪 Hướng dẫn chạy dự án

1. **Yêu cầu:**
   - Java 21+
   - Maven
   - IDE (VS Code / IntelliJ)
   -  SQL Server



3. **Chạy app:**
   ```bash
   ./mvnw spring-boot:run
   ```

---

## 👥 Thành viên nhóm

- Trưởng nhóm: ...
- Thành viên: ...

---

## 📌 Ghi chú cho team

- Mọi thay đổi **phải tạo nhánh riêng** và mở **pull request**.
- Đặt tên nhánh: `feature/<tên>`, `fix/<tên>`, `refactor/<tên>`
- Thường xuyên **pull từ nhánh main** để cập nhật code mới.
