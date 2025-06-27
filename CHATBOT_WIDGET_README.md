# OLearning Chatbot Widget

## Tổng quan
Chatbot Widget là một tính năng AI trợ lý được tích hợp trực tiếp vào trang web OLearning, cho phép người dùng tương tác với AI để được hỗ trợ về khóa học, hướng dẫn học tập và giải đáp thắc mắc.

## Cách sử dụng

### 1. Truy cập Chatbot Widget
- Nhấn vào nút tròn màu xanh có biểu tượng robot ở góc dưới bên phải màn hình
- Nút này luôn hiển thị và dễ truy cập nhất

### 2. Tương tác với Chatbot
- **Gửi tin nhắn**: Nhập câu hỏi vào ô input và nhấn Enter hoặc nút gửi
- **Thu nhỏ**: Nhấn nút "-" để thu nhỏ widget
- **Phóng to**: Nhấn nút "↗" để phóng to widget từ trạng thái thu nhỏ
- **Đóng**: Nhấn nút "×" để đóng hoàn toàn
- **Xóa lịch sử**: Nhấn nút "🗑" để xóa toàn bộ lịch sử chat

### 3. Tính năng
- **Chat thời gian thực**: Tương tác trực tiếp với Google AI
- **Lưu lịch sử**: Tự động lưu lịch sử chat theo session
- **Hỗ trợ đa người dùng**: Hoạt động cho cả user đã đăng nhập và khách
- **Responsive**: Tương thích với mọi thiết bị
- **Thông báo**: Hiển thị badge thông báo khi có tin nhắn mới

## API Endpoints

### 1. Gửi tin nhắn
```
POST /chat/send
Content-Type: application/json

{
    "message": "Nội dung tin nhắn",
    "sessionId": "session-id"
}
```

### 2. Lấy lịch sử chat theo session
```
GET /chat/history/{sessionId}
```

### 3. Lấy lịch sử chat của user
```
GET /chat/user-history
```

### 4. Xóa lịch sử chat
```
DELETE /chat/clear/{sessionId}
```

### 5. Kiểm tra trạng thái
```
GET /chat/status
```

## Cấu trúc dữ liệu

### ChatRequestDTO
```java
{
    "message": "String",
    "sessionId": "String"
}
```

### ChatResponseDTO
```java
{
    "message": "String",
    "sessionId": "String", 
    "timestamp": "LocalDateTime",
    "success": "boolean",
    "error": "String"
}
```

### ChatMessage Entity
```java
{
    "id": "Long",
    "sessionId": "String",
    "userMessage": "String",
    "aiResponse": "String",
    "messageType": "MessageType",
    "user": "User",
    "createdAt": "LocalDateTime"
}
```

## Cấu hình

### 1. Google AI API
Cấu hình trong `application.properties`:
```properties
google.ai.api.key=your-api-key
google.ai.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent
```

### 2. Database
Tự động tạo bảng `chat_messages` khi khởi động ứng dụng.

## Tính năng nâng cao

### 1. Context Awareness
- Chatbot nhớ lịch sử 10 tin nhắn gần nhất để tạo context
- Hỗ trợ cuộc hội thoại liên tục

### 2. User Authentication
- Tự động liên kết chat với user đã đăng nhập
- Lưu lịch sử chat theo user ID

### 3. Session Management
- Tạo session ID tự động cho mỗi phiên chat
- Hỗ trợ nhiều phiên chat đồng thời

### 4. Error Handling
- Xử lý lỗi API gracefully
- Hiển thị thông báo lỗi thân thiện
- Retry mechanism cho network issues

## Troubleshooting

### 1. Chatbot không hiển thị
- Kiểm tra console browser có lỗi JavaScript không
- Đảm bảo file chatbotWidget.html được include trong mainContent.html

### 2. Không gửi được tin nhắn
- Kiểm tra kết nối internet
- Kiểm tra Google AI API key có hợp lệ không
- Xem log server để debug

### 3. Lịch sử chat không load
- Kiểm tra session ID có được tạo đúng không
- Kiểm tra database connection

## Bảo mật

- API key được bảo vệ trong server-side
- Input validation cho tất cả tin nhắn
- Rate limiting để tránh spam
- Session isolation giữa các user

## Performance

- Lazy loading cho lịch sử chat
- Debouncing cho input events
- Optimized database queries
- Caching cho frequent responses

## Tương lai

- [ ] Voice input/output
- [ ] File upload support
- [ ] Multi-language support
- [ ] Advanced analytics
- [ ] Custom AI models
- [ ] Integration với LMS features

## Liên hệ
Nếu có vấn đề hoặc câu hỏi, vui lòng liên hệ team phát triển OLearning. 