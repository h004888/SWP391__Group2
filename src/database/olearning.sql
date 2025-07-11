--CREATE DATABASE OLearning_New;
--GO

--USE OLearning_New;
--GO

-- Roles:
CREATE TABLE Roles (
                       RoleID SMALLINT IDENTITY(1,1) PRIMARY KEY,
                       Name NVARCHAR(20) NOT NULL UNIQUE
);
GO

-- Users: 
CREATE TABLE Users (
                       UserID INT IDENTITY(1,1) PRIMARY KEY,
                       Username NVARCHAR(50) NOT NULL UNIQUE,
                       RoleID SMALLINT NULL,
                       Email NVARCHAR(100) NOT NULL UNIQUE,
                       Password NVARCHAR(255) NOT NULL,        -- mật khẩu đã hash
                       FullName NVARCHAR(100) ,
                       Phone NVARCHAR(15) NULL,
                       Coin DECIMAL(10,2) NULL DEFAULT 0 CHECK (Coin >= 0),
                       Birthday DATETIME NULL,
                       Address NVARCHAR(255) NULL,
                       ProfilePicture NVARCHAR(MAX) NULL,
                       Status BIT NOT NULL DEFAULT 1,
                       PersonalSkill NVARCHAR(MAX) NULL,
                       FOREIGN KEY (RoleID) REFERENCES Roles(RoleID)
);
GO

-- Categories
CREATE TABLE Categories (
                            CategoryID INT IDENTITY(1,1) PRIMARY KEY,
                            Name NVARCHAR(255) NOT NULL
);
GO

-- Courses: Bảng khóa học
CREATE TABLE Courses (
                         CourseID INT IDENTITY(1,1) PRIMARY KEY,
                         InstructorID INT NOT NULL,
                         CategoryID INT NULL,
                         Title NVARCHAR(255) NOT NULL,
                         Description NVARCHAR(max) NULL,
                         Price DECIMAL(10,2) NULL DEFAULT 0,
                         Discount DECIMAL(5,2) NULL DEFAULT 0,
                         CourseImg NVARCHAR(255) NULL,
                         CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
                         UpdatedAt DATETIME NULL DEFAULT GETDATE(),
                         Status NVARCHAR(20) NULL CHECK (Status IN (
        'pending','approved','rejected','resubmit','live','blocked'
    )),
                         CanResubmit BIT NULL DEFAULT 1,
                         VideoUrlPreview NVARCHAR(MAX) NULL,
                         IsFree BIT NULL,
                         CourseLevel VARCHAR(255) NULL,
                         FOREIGN KEY (InstructorID) REFERENCES Users(UserID),
                         FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID)
);
GO

-- Chapters
CREATE TABLE Chapters (
                          ChapterID INT IDENTITY(1,1) PRIMARY KEY,
                          CourseID INT NOT NULL,
                          Title NVARCHAR(255) NOT NULL,
                          Description NVARCHAR(500) NULL,
                          OrderNumber INT NOT NULL DEFAULT 0,
                          CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
                          UpdatedAt DATETIME  NULL,
                          FOREIGN KEY (CourseID) REFERENCES Courses(CourseID) ON DELETE CASCADE
);
GO

-- Lessons
CREATE TABLE Lessons (
                         LessonID INT IDENTITY(1,1) PRIMARY KEY,
                         ChapterID INT NOT NULL,
                         Title NVARCHAR(255) NOT NULL,
                         Description NVARCHAR(max) NULL,
                         ContentType NVARCHAR(20) NULL CHECK (
        ContentType IN ('video','document','quiz')
    ),
                         Duration INT NULL DEFAULT 0,
                         IsFree BIT NOT NULL DEFAULT 0,
                         OrderNumber INT NOT NULL DEFAULT 0,
                         CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
                         UpdatedAt DATETIME NULL,
                         FOREIGN KEY (ChapterID) REFERENCES Chapters(ChapterID) ON DELETE CASCADE
);
GO

-- Quizzes
CREATE TABLE Quiz_test (
                           QuizID INT IDENTITY(1,1) PRIMARY KEY,
                           LessonID INT NOT NULL,
                           Title VARCHAR(255) NULL,
                           Description VARCHAR(max) NULL,
    TimeLimit INT NULL,
    CreatedAt DATETIME2(6) NULL,
    UpdatedAt DATETIME NULL,
    FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID) ON DELETE CASCADE
);
GO

-- QuizQuestions
CREATE TABLE Quiz_questions (
                                QuestionID INT IDENTITY(1,1) PRIMARY KEY,
                                QuizID INT NOT NULL,
                                Question NVARCHAR(MAX) NULL,
                                OptionA NVARCHAR(255) NULL,
                                OptionB NVARCHAR(255) NULL,
                                OptionC NVARCHAR(255) NULL,
                                OptionD NVARCHAR(255) NULL,
                                CorrectAnswer VARCHAR(255) NULL,
                                OrderNumber INT NULL,
                                FOREIGN KEY (QuizID) REFERENCES Quiz_test(QuizID) ON DELETE CASCADE
);
GO

-- Video: Bảng video
CREATE TABLE Video (
                       VideoID INT IDENTITY(1,1) PRIMARY KEY,
                       LessonID INT NOT NULL,
                       VideoURL NVARCHAR(1000) NOT NULL,
                       Duration INT NULL, -- Nên là INT (giây) thay vì TIME để dễ tính toán.
                       UploadDate DATETIME NOT NULL DEFAULT GETDATE(),
                       FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID) ON DELETE CASCADE
);
GO

-- CoinTransaction: Bảng giao dịch xu
CREATE TABLE CoinTransaction (
                                 TransactionID INT IDENTITY(1,1) PRIMARY KEY,
                                 UserID INT NOT NULL,
                                 Amount DECIMAL(10,2) NOT NULL,
                                 TransactionType NVARCHAR(30) NOT NULL CHECK (
        TransactionType IN (
            'top_up','course_purchase','creation_fee',
            'maintenance_fee','withdraw'
        )
    ),
                                 Status NVARCHAR(20) NOT NULL CHECK (Status IN ('completed','failed')),
                                 Note NVARCHAR(max) NULL,
                                 CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
                                 FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
GO

-- Orders: Bảng đơn hàng
CREATE TABLE Orders (
                        OrderID INT IDENTITY(1,1) PRIMARY KEY,
                        UserID INT NOT NULL,
                        Amount DECIMAL(10,2) NOT NULL,
                        OrderType NVARCHAR(50) NOT NULL CHECK (
        OrderType IN ('course_purchase','creation_fee','maintenance_fee')
    ),
                        Status NVARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (
        Status IN ('pending','completed')
    ),
                        OrderDate DATETIME NOT NULL DEFAULT GETDATE(),
                        RefCode NVARCHAR(500) NULL,
                        FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
GO

-- OrderDetail
CREATE TABLE OrderDetail (
                             OrderDetailID INT IDENTITY(1,1) PRIMARY KEY,
                             OrderID INT NOT NULL,
                             CourseID INT NOT NULL,
                             UnitPrice DECIMAL(10,2) NOT NULL,
                             FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
                             FOREIGN KEY (CourseID) REFERENCES Courses(CourseID),
                             CONSTRAINT UQ_OrderDetail_Order_Course UNIQUE (OrderID, CourseID)
);
GO

-- Enrollments: Bảng ghi danh khóa học
CREATE TABLE Enrollments (
                             EnrollmentID INT IDENTITY(1,1) PRIMARY KEY, -- Khóa chính tự tăng của riêng bảng này.
                             UserID INT NOT NULL,                        -- Khóa ngoại tới người dùng (học viên).
                             CourseID INT NOT NULL,                      -- Khóa ngoại tới khóa học.
                             EnrollmentDate DATETIME NOT NULL DEFAULT GETDATE(),
                             Progress DECIMAL(5,2) NOT NULL DEFAULT 0,
                             Status NVARCHAR(20) NOT NULL DEFAULT 'onGoing' CHECK (
        Status IN ('onGoing','completed')
    ),
                             OrderID INT NULL,                           -- Liên kết tới đơn hàng mua khóa học (nếu có).

                             FOREIGN KEY (UserID) REFERENCES Users(UserID),
                             FOREIGN KEY (CourseID) REFERENCES Courses(CourseID),
                             FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),

                             CONSTRAINT UQ_User_Course UNIQUE (UserID, CourseID)
);
GO

-- CourseReviews: Bảng đánh giá khóa học
CREATE TABLE CourseReviews (
                               ReviewID INT IDENTITY(1,1) PRIMARY KEY,
                               CourseID INT NOT NULL,
                               EnrollmentID INT NOT NULL,
                               Rating INT NULL CHECK (Rating BETWEEN 1 AND 5),
                               Comment NVARCHAR(1000) NULL,
                               CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
                               UpdatedAt DATETIME NULL DEFAULT GETDATE(),

                               FOREIGN KEY (CourseID) REFERENCES Courses(CourseID),
                               FOREIGN KEY (EnrollmentID) REFERENCES Enrollments(EnrollmentID),
);
GO

-- Fees: Bảng các mức phí
CREATE TABLE Fees (
                      FeeID INT IDENTITY(1,1) PRIMARY KEY,
                      MinEnrollments BIGINT NOT NULL,
                      MaxEnrollments BIGINT NULL,
                      MaintenanceFee BIGINT NOT NULL,
                      UNIQUE (MinEnrollments, MaxEnrollments)
);
GO

-- CourseMaintenance
CREATE TABLE CourseMaintenance (
                                   MaintenanceID INT IDENTITY(1,1) PRIMARY KEY,
                                   CourseID INT NOT NULL,
                                   FeeID INT NOT NULL,
                                   OrderID INT NULL,
                                   MonthYear DATE NOT NULL,
                                   EnrollmentCount BIGINT NOT NULL,
                                   Status VARCHAR(10) DEFAULT 'pending' CHECK (status IN ('pending', 'completed','overdue')),
                                   DueDate DATE NOT NULL,
                                   SentAt DATETIME DEFAULT GETDATE(),
                                   FOREIGN KEY (CourseID) REFERENCES Courses(CourseID),
                                   FOREIGN KEY (FeeID) REFERENCES Fees(FeeID),
                                   FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
                                   UNIQUE (CourseID, MonthYear)
);
GO

-- Notifications: Bảng thông báo
CREATE TABLE Notifications (
                               NotificationID INT IDENTITY(1,1) PRIMARY KEY,
                               UserID INT NOT NULL,
                               CourseID INT NULL,
                               Type NVARCHAR(30) NOT NULL CHECK (
        Type IN ('MAINTENANCE_FEE','COURSE_REJECTION','ACCOUNT_LOCK','ACCOUNT_UNLOCK')
    ),
                               Status NVARCHAR(10) NOT NULL DEFAULT 'sent' CHECK (
        Status IN ('sent','failed')
    ),
                               Message NVARCHAR(MAX) NOT NULL,
                               SentAt DATETIME NOT NULL DEFAULT GETDATE(),
                               CommentID INT NULL,
                               FOREIGN KEY (UserID) REFERENCES Users(UserID),
                               FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);
GO

-- PasswordResetOTP: 
CREATE TABLE PasswordResetOTP (
                                  id BIGINT IDENTITY(1,1) PRIMARY KEY,
                                  user_userId INT NOT NULL,
                                  otp NVARCHAR(10) NOT NULL,
                                  expiry_time DATETIME2 NOT NULL,
                                  attempts INT NOT NULL DEFAULT 0,
                                  verified BIT NOT NULL DEFAULT 0,
                                  created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
                                  updated_at DATETIME2 NULL,
                                  CONSTRAINT FK_PasswordResetOTP_User
                                      FOREIGN KEY (user_userId) REFERENCES Users(UserID) ON DELETE CASCADE
);
GO

-- InstructorRequests: Bảng yêu cầu làm giảng viên
CREATE TABLE InstructorRequests (
                                    RequestID INT IDENTITY(1,1) PRIMARY KEY,
                                    UserID INT NOT NULL,
                                    RequestDate DATETIME NOT NULL DEFAULT GETDATE(),
                                    Status NVARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (
        Status IN ('pending','approved','rejected')
    ),
                                    Note NVARCHAR(1000) NULL,
                                    AdminID INT NULL, -- Admin xử lý yêu cầu
                                    DecisionDate DATETIME NULL,
                                    FOREIGN KEY (UserID) REFERENCES Users(UserID),
                                    FOREIGN KEY (AdminID) REFERENCES Users(UserID)
);
GO
CREATE TABLE LessonCompletion (
                                  CompletionID INT IDENTITY(1,1) PRIMARY KEY,
                                  UserID INT NOT NULL,
                                  LessonID INT NOT NULL,
                                  CompletedAt DATETIME NOT NULL DEFAULT GETDATE(),
                                  FOREIGN KEY (UserID) REFERENCES Users(UserID),
                                  FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID),
                                  CONSTRAINT UQ_User_Lesson UNIQUE (UserID, LessonID)
);
GO
USE [OLearning_New]
GO

-- =============================================
-- BƯỚC 1: CHÈN DỮ LIỆU CHO CÁC BẢNG KHÔNG CÓ KHÓA NGOẠI (BẢNG GỐC)
-- =============================================

-- Bảng: Roles
SET IDENTITY_INSERT [dbo].[Roles] ON
GO
INSERT [dbo].[Roles] ([RoleID], [Name]) VALUES (1, N'Admin')
GO
INSERT [dbo].[Roles] ([RoleID], [Name]) VALUES (2, N'Instructor')
GO
INSERT [dbo].[Roles] ([RoleID], [Name]) VALUES (3, N'User')
GO
SET IDENTITY_INSERT [dbo].[Roles] OFF
GO

-- Bảng: Categories
SET IDENTITY_INSERT [dbo].[Categories] ON
GO
INSERT [dbo].[Categories] ([CategoryID], [Name]) VALUES (1, N'Lập trình Web')
GO
INSERT [dbo].[Categories] ([CategoryID], [Name]) VALUES (2, N'Lập trình Di động')
GO
INSERT [dbo].[Categories] ([CategoryID], [Name]) VALUES (3, N'Ngôn ngữ lập trình')
GO
INSERT [dbo].[Categories] ([CategoryID], [Name]) VALUES (4, N'Cơ sở dữ liệu')
GO
INSERT [dbo].[Categories] ([CategoryID], [Name]) VALUES (5, N'Khoa học Dữ liệu & AI')
GO
SET IDENTITY_INSERT [dbo].[Categories] OFF
GO

-- Bảng: Fees
SET IDENTITY_INSERT [dbo].[Fees] ON
GO
INSERT [dbo].[Fees] ([FeeID], [MaintenanceFee], [MaxEnrollments], [MinEnrollments]) VALUES (1, 90000, 30, 0)
GO
INSERT [dbo].[Fees] ([FeeID], [MaintenanceFee], [MaxEnrollments], [MinEnrollments]) VALUES (2, 150000, 80, 31)
GO
INSERT [dbo].[Fees] ([FeeID], [MaintenanceFee], [MaxEnrollments], [MinEnrollments]) VALUES (3, 300000, 200, 101)
GO
INSERT [dbo].[Fees] ([FeeID], [MaintenanceFee], [MaxEnrollments], [MinEnrollments]) VALUES (4, 400000, 300, 201)
GO
INSERT [dbo].[Fees] ([FeeID], [MaintenanceFee], [MaxEnrollments], [MinEnrollments]) VALUES (5, 500000, 500, 301)
GO
INSERT [dbo].[Fees] ([FeeID], [MaintenanceFee], [MaxEnrollments], [MinEnrollments]) VALUES (6, 500000, NULL, 501)
GO
SET IDENTITY_INSERT [dbo].[Fees] OFF
GO

-- TermsAndConditions: Bảng điều khoản và điều kiện
CREATE TABLE TermsAndConditions (
    id INT IDENTITY(1,1) PRIMARY KEY,
    RoleTarget NVARCHAR(20) NOT NULL,
    SectionTitle NVARCHAR(255) NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    DisplayOrder INT NULL,
    CreatedAt DATETIME2 NULL,
    UpdatedAt DATETIME2 NULL
);
GO

-- LƯU Ý: Lệnh CREATE TABLE cho [TermsAndConditions] không có trong script bạn cung cấp.
-- Các lệnh INSERT dưới đây giả định bảng này đã tồn tại và không có khóa ngoại. Nếu chưa có, bạn cần tạo bảng này trước.
SET IDENTITY_INSERT [dbo].[TermsAndConditions] ON
 GO
 INSERT [dbo].[TermsAndConditions] ([DisplayOrder], [CreatedAt], [UpdatedAt], [id], [RoleTarget], [SectionTitle], [Content]) VALUES (1, CAST(N'2025-06-23T15:20:26.3700000' AS DateTime2), CAST(N'2025-06-24T00:18:41.0893160' AS DateTime2), 1, N'ALL', N'Giới thiệu', N'<ul><li><strong>Bằng việc sử dụng nền tảng của chúng tôi, bạn đồng ý tuân thủ các điều khoản dưới đây.</strong></li><li><strong>Các điều khoản có thể được cập nhật bất kỳ lúc nào và sẽ được thông báo công khai.</strong></li></ul>')
 GO
 INSERT [dbo].[TermsAndConditions] ([DisplayOrder], [CreatedAt], [UpdatedAt], [id], [RoleTarget], [SectionTitle], [Content]) VALUES (2, CAST(N'2025-06-23T15:20:26.3700000' AS DateTime2), CAST(N'2025-06-24T00:18:48.0227510' AS DateTime2), 2, N'ALL', N'Quy định chung', N'<ul><li><strong>Người dùng phải cung cấp thông tin chính xác khi đăng ký.</strong></li><li><strong>Mỗi tài khoản chỉ được sử dụng bởi một cá nhân.</strong></li><li><strong>Nghiêm cấm mọi hành vi gian lận, giả mạo, vi phạm pháp luật.</strong></li></ul>')
 GO
 INSERT [dbo].[TermsAndConditions] ([DisplayOrder], [CreatedAt], [UpdatedAt], [id], [RoleTarget], [SectionTitle], [Content]) VALUES (3, CAST(N'2025-06-23T15:20:26.3700000' AS DateTime2), CAST(N'2025-06-24T00:18:57.9766370' AS DateTime2), 3, N'USER', N'Quy định dành cho Người học', N'<ul><li><strong>Được phép đăng ký, học và đánh giá khóa học.</strong></li><li><strong>Không được sao chép, ghi hình, chia sẻ nội dung khóa học dưới bất kỳ hình thức nào.</strong></li><li><strong>Được hoàn tiền sau khi đã thanh toán trong vòng 3 ngày hoặc khi có lỗi từ hệ thống hoặc khóa học bị hủy bỏ.</strong></li></ul>')
 GO
INSERT [dbo].[TermsAndConditions] ([DisplayOrder], [CreatedAt], [UpdatedAt], [id], [RoleTarget], [SectionTitle], [Content]) VALUES (4, CAST(N'2025-06-23T15:20:26.3700000' AS DateTime2), CAST(N'2025-06-24T00:19:37.2572990' AS DateTime2), 4, N'INSTRUCTOR', N'Quy định dành cho Người dạy', N'<p><strong>📝 Tạo khóa học</strong></p><ul><li><strong>Mỗi khóa học khi tạo sẽ mất phí tạo ban đầu là 100.000 VNĐ / khóa.</strong></li><li><strong>Phí này chỉ thu sau khi khóa học được admin phê duyệt.</strong></li><li><strong>Nếu khóa học bị từ chối, bạn không bị trừ tiền.</strong></li></ul><p><strong>💰 Phí bảo trì hàng tháng</strong></p><p><strong>Phí bảo trì phụ thuộc vào số lượng học viên đăng ký cho từng khóa học:</strong></p><figure class="table"><table><thead><tr><th>Số lượng học viên</th><th>Phí bảo trì / tháng</th></tr></thead><tbody><tr><td>0 – 30 học viên</td><td>100.000 VNĐ</td></tr><tr><td>31 – 80 học viên</td><td>150.000 VNĐ</td></tr><tr><td>81 – 150 học viên</td><td>250.000 VNĐ</td></tr><tr><td>151 – 300 học viên</td><td>400.000 VNĐ</td></tr><tr><td>> 300 học viên</td><td>600.000 VNĐ</td></tr></tbody></table></figure><p><strong>Ghi chú:</strong> <strong>Mỗi tháng hệ thống sẽ thống kê tổng số học viên theo từng khóa học để tính mức phí bảo trì tương ứng.</strong></p><p><strong>⏰ Thời hạn thanh toán</strong></p><ul><li><strong>Hóa đơn được tạo vào ngày 1 hàng tháng.</strong></li><li><strong>Thời hạn thanh toán là 7 ngày.</strong></li><li><strong>Nếu quá hạn:</strong><ul><li><strong>+7 ngày: Khóa học bị ẩn khỏi hệ thống.</strong></li><li><strong>+14 ngày: Không được tạo khóa học mới.</strong></li><li><strong>+30 ngày: Tài khoản bị khóa tạm thời, cần liên hệ quản trị viên để xử lý.</strong></li></ul></li></ul>')
 GO
 INSERT [dbo].[TermsAndConditions] ([DisplayOrder], [CreatedAt], [UpdatedAt], [id], [RoleTarget], [SectionTitle], [Content]) VALUES (5, CAST(N'2025-06-23T15:20:26.3700000' AS DateTime2), CAST(N'2025-06-24T00:19:59.2328390' AS DateTime2), 5, N'ALL', N'Chính sách thanh toán', N'<ul><li><strong>Thanh toán qua các hình thức như chuyển khoản, QR code, ví điện tử.</strong></li><li><strong>Lưu trữ hóa đơn trong hệ thống và có thể tra cứu bất kỳ lúc nào.</strong></li><li><strong>Không hoàn tiền sau khi giao dịch đã xác nhận thành công.</strong></li></ul>')
 GO
 INSERT [dbo].[TermsAndConditions] ([DisplayOrder], [CreatedAt], [UpdatedAt], [id], [RoleTarget], [SectionTitle], [Content]) VALUES (6, CAST(N'2025-06-23T15:20:26.3700000' AS DateTime2), CAST(N'2025-06-24T00:20:08.7263630' AS DateTime2), 6, N'ALL', N'Khóa tài khoản', N'<ul><li><strong>Người dạy cần thanh toán toàn bộ các khoản phí đang nợ, nếu quá hạn trong thời gian dài, tài khoản sẽ bị khóa.</strong></li><li><strong>Vi phạm quy định sẽ bị khóa tài khoản vĩnh viễn.</strong></li></ul>')
 GO
INSERT [dbo].[TermsAndConditions] ([DisplayOrder], [CreatedAt], [UpdatedAt], [id], [RoleTarget], [SectionTitle], [Content]) VALUES (7, CAST(N'2025-06-23T15:20:26.3700000' AS DateTime2), CAST(N'2025-06-24T00:20:18.2451380' AS DateTime2), 7, N'ALL', N'Cam kết hệ thống', N'<ul><li><strong>Bảo mật thông tin cá nhân.</strong></li><li><strong>Không chia sẻ dữ liệu với bên thứ ba.</strong></li><li><strong>Hỗ trợ 24/7 qua email hoặc kênh liên hệ chính thức.</strong></li></ul>')
 GO
 INSERT [dbo].[TermsAndConditions] ([DisplayOrder], [CreatedAt], [UpdatedAt], [id], [RoleTarget], [SectionTitle], [Content]) VALUES (8, CAST(N'2025-06-23T15:20:26.3700000' AS DateTime2), NULL, 8, N'ALL', N'Trách nhiệm pháp lý', N'<ul>  <li>Người dùng chịu trách nhiệm với mọi nội dung do mình tạo ra.</li>  <li>Trong trường hợp tranh chấp, quyết định cuối cùng thuộc về ban quản trị.</li></ul>')
 GO
INSERT [dbo].[TermsAndConditions] ([DisplayOrder], [CreatedAt], [UpdatedAt], [id], [RoleTarget], [SectionTitle], [Content]) VALUES (9, CAST(N'2025-06-23T15:20:26.3700000' AS DateTime2), CAST(N'2025-06-24T00:20:34.1598880' AS DateTime2), 9, N'ALL', N'Quyền sở hữu trí tuệ', N'<ul><li><strong>Toàn bộ nội dung (video, tài liệu, hình ảnh, âm thanh) trên nền tảng thuộc bản quyền của Người dạy hoặc hệ thống.</strong></li><li><strong>Nghiêm cấm sao chép, tái sản xuất, phân phối hoặc chuyển giao dưới mọi hình thức khi chưa có sự cho phép bằng văn bản.</strong></li><li><strong>Mọi vi phạm về bản quyền sẽ bị xử lý theo quy định pháp luật.</strong></li></ul>')
GO
INSERT [dbo].[TermsAndConditions] ([DisplayOrder], [CreatedAt], [UpdatedAt], [id], [RoleTarget], [SectionTitle], [Content]) VALUES (10, CAST(N'2025-06-23T15:20:26.3700000' AS DateTime2), CAST(N'2025-06-24T00:20:26.4984990' AS DateTime2), 10, N'ALL', N'Chính sách bảo mật & riêng tư', N'<ul><li><strong>Cam kết bảo mật tuyệt đối thông tin cá nhân của Người dùng.</strong></li><li><strong>Chỉ sử dụng thông tin để nâng cao chất lượng dịch vụ và không chia sẻ cho bên thứ ba trừ khi được pháp luật yêu cầu.</strong></li><li><strong>Người dùng có quyền yêu cầu xem, chỉnh sửa hoặc xóa thông tin cá nhân bất kỳ lúc nào.</strong></li></ul>')
GO
INSERT [dbo].[TermsAndConditions] ([DisplayOrder], [CreatedAt], [UpdatedAt], [id], [RoleTarget], [SectionTitle], [Content]) VALUES (11, CAST(N'2025-06-23T15:20:26.3700000' AS DateTime2), CAST(N'2025-06-24T00:20:41.8747370' AS DateTime2), 11, N'ALL', N'Cam kết chất lượng & hỗ trợ', N'<ul><li><strong>Đảm bảo nền tảng hoạt động ổn định ≥ 99.5% thời gian trong tháng.</strong></li><li><strong>Thông báo kế hoạch bảo trì ít nhất 48 giờ trước khi thực hiện.</strong></li><li><strong>Hỗ trợ kỹ thuật và giải đáp thắc mắc 24/7 qua email, chat hoặc hotline.</strong></li></ul>')
GO
INSERT [dbo].[TermsAndConditions] ([DisplayOrder], [CreatedAt], [UpdatedAt], [id], [RoleTarget], [SectionTitle], [Content]) VALUES (12, CAST(N'2025-06-23T15:20:26.3700000' AS DateTime2), CAST(N'2025-06-24T00:20:51.4797310' AS DateTime2), 12, N'ALL', N'Thay đổi và cập nhật nội dung', N'<ul><li><strong>Hệ thống có quyền điều chỉnh, cập nhật giao diện, tính năng, cấu trúc khóa học mà không cần thông báo trước đối với các cải tiến kỹ thuật.</strong></li><li><strong>Mọi thay đổi liên quan đến phí, quy định hoặc chính sách sẽ được công bố công khai ít nhất 7 ngày trước khi có hiệu lực.</strong></li></ul>')
GO
INSERT [dbo].[TermsAndConditions] ([DisplayOrder], [CreatedAt], [UpdatedAt], [id], [RoleTarget], [SectionTitle], [Content]) VALUES (13, CAST(N'2025-06-23T15:20:26.3700000' AS DateTime2), CAST(N'2025-06-24T00:20:59.7170490' AS DateTime2), 13, N'ALL', N'Chấm dứt hợp tác', N'<ul><li><strong>Hệ thống có quyền chấm dứt hợp tác với Người dạy hoặc khóa học vi phạm nhiều lần mà không hoàn tiền phí đã thu.</strong></li><li><strong>Người học vi phạm nghiêm trọng quy định (gian lận, chia sẻ bất hợp pháp) có thể bị khóa tài khoản vĩnh viễn.</strong></li><li><strong>Khi chấm dứt, mọi quyền truy cập khóa học sẽ bị thu hồi ngay lập tức.</strong></li></ul>')
GO
INSERT [dbo].[TermsAndConditions] ([DisplayOrder], [CreatedAt], [UpdatedAt], [id], [RoleTarget], [SectionTitle], [Content]) VALUES (14, CAST(N'2025-06-23T15:20:26.3700000' AS DateTime2), CAST(N'2025-06-24T00:19:49.4640970' AS DateTime2), 14, N'ALL', N'Giải quyết tranh chấp', N'<ul><li><strong>Mọi tranh chấp sẽ được thương lượng, hòa giải tại văn phòng Trung tâm trước khi đưa ra cơ quan tố tụng.</strong></li><li><strong>Nếu không hòa giải được, vụ việc sẽ được giải quyết tại Tòa án nhân dân có thẩm quyền tại TP. Hà Nội.</strong></li></ul>')
GO
INSERT [dbo].[TermsAndConditions] ([DisplayOrder], [CreatedAt], [UpdatedAt], [id], [RoleTarget], [SectionTitle], [Content]) VALUES (15, CAST(N'2025-06-23T15:20:26.3700000' AS DateTime2), CAST(N'2025-06-24T00:04:25.0781360' AS DateTime2), 15, N'ALL', N'Điều khoản bất khả kháng', N'<ul><li><strong>Không bên nào chịu trách nhiệm bồi thường thiệt hại do sự kiện bất khả kháng (thiên tai, chiến tranh, dịch bệnh, sự cố mạng…) ngoài khả năng kiểm soát.</strong></li><li><strong>Bên gặp sự kiện phải thông báo cho bên kia trong vòng 5 ngày kể từ thời điểm xảy ra.</strong></li></ul>')
GO
SET IDENTITY_INSERT [dbo].[TermsAndConditions] OFF
GO

-- =============================================
-- BƯỚC 2: CHÈN DỮ LIỆU CHO CÁC BẢNG PHỤ THUỘC VÀO BƯỚC 1
-- =============================================

-- Bảng: Users (Phụ thuộc vào Roles)
SET IDENTITY_INSERT [dbo].[Users] ON
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (NULL, 0, 1, 1, 1, NULL, N'superadmin@gmail.com', N'Trần Quản Trị', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0905111222', NULL, N'superadmin')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (NULL, 0, 1, 1, 2, NULL, N'admin.anh@gmail.com', N'Nguyễn Tuấn Anh', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0905333444', NULL, N'admin.anh')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (NULL, 0, 1, 1, 3, NULL, N'admin.binh@gmail.com', N'Lê Hòa Bình', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0905555666', NULL, N'admin.binh')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (NULL, 0, 1, 1, 4, NULL, N'admin.cuong@gmail.com', N'Phạm Việt Cường', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0905777888', NULL, N'admin.cuong')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (NULL, 0, 1, 1, 5, NULL, N'admin.dung@gmail.com', N'Võ Tiến Dũng', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0905999000', NULL, N'admin.dung')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (NULL, 0, 1, 2, 6, NULL, N'ins.minhanh@gmail.com', N'Giảng viên Minh Anh', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', N'Lập trình Java, Spring Boot, Microservices', N'0987111222', NULL, N'ins.minhanh')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (NULL, 0, 1, 2, 7, NULL, N'ins.quanghuy@gmail.com', N'Giảng viên Quang Huy', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', N'Lập trình Python, Django, Data Science', N'0987222333', NULL, N'ins.quanghuy')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (NULL, 0, 1, 2, 8, NULL, N'ins.phuongthao@gmail.com', N'Giảng viên Phương Thảo', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', N'Frontend Development, ReactJS, VueJS', N'0987333444', NULL, N'ins.phuongthao')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (NULL, 0, 1, 2, 9, NULL, N'ins.thanhson@gmail.com', N'Giảng viên Thành Sơn', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', N'Database Design, SQL Server, Oracle', N'0987444555', NULL, N'ins.thanhson')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (NULL, 0, 1, 2, 10, NULL, N'ins.haivan@gmail.com', N'Giảng viên Hải Vân', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', N'UI/UX Design, Figma, Adobe XD', N'0987555666', NULL, N'ins.haivan')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (NULL, 0, 1, 2, 11, NULL, N'ins.tuanhung@gmail.com', N'Giảng viên Tuấn Hưng', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', N'Node.js, Express, MongoDB', N'0987666777', NULL, N'ins.tuanhung')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (NULL, 0, 1, 2, 12, NULL, N'ins.ngoclan@gmail.com', N'Giảng viên Ngọc Lan', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', N'DevOps, Docker, Kubernetes, CI/CD', N'0987777888', NULL, N'ins.ngoclan')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (NULL, 0, 1, 2, 13, NULL, N'ins.giabao@gmail.com', N'Giảng viên Gia Bảo', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', N'Mobile App Development, Flutter, Dart', N'0987888999', NULL, N'ins.giabao')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (NULL, 0, 1, 2, 14, NULL, N'ins.kimlien@gmail.com', N'Giảng viên Kim Liên', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', N'Project Management, Agile, Scrum', N'0987999000', NULL, N'ins.kimlien')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (NULL, 0, 1, 2, 15, NULL, N'ins.ducanh@gmail.com', N'Giảng viên Đức Anh', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', N'C#, .NET Core, ASP.NET', N'0987000111', NULL, N'ins.ducanh')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (CAST(N'1998-05-20' AS Date), 150, 1, 3, 16, N'123 Đường Lê Lợi, Quận 1, TP.HCM', N'nguyenvana@gmail.com', N'Nguyễn Văn An', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0912345678', NULL, N'nguyenvana')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (CAST(N'1999-11-15' AS Date), 25.5, 1, 3, 17, N'45 Đường Nguyễn Huệ, Quận 1, TP.HCM', N'tranthib@gmail.com', N'Trần Thị Bích', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0912345679', NULL, N'tranthib')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (CAST(N'2001-01-30' AS Date), 500, 1, 3, 18, N'212 Đường Hai Bà Trưng, Quận 3, TP.HCM', N'leminhc@gmail.com', N'Lê Minh Chiến', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0912345680', NULL, N'leminhc')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (CAST(N'2002-07-10' AS Date), 0, 1, 3, 19, N'18 Lý Tự Trọng, Quận Hoàn Kiếm, Hà Nội', N'phamthud@gmail.com', N'Phạm Thu Dung', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0912345681', NULL, N'phamthud')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (CAST(N'1995-03-22' AS Date), 75, 1, 3, 20, N'33 Trần Phú, Quận Hải Châu, Đà Nẵng', N'hoangvanh@gmail.com', N'Hoàng Văn Hải', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0912345682', NULL, N'hoangvanh')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (CAST(N'2000-09-01' AS Date), 120.25, 1, 3, 21, N'56 Hùng Vương, Quận Ninh Kiều, Cần Thơ', N'dothuyk@gmail.com', N'Đỗ Thúy Kiều', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0912345683', NULL, N'dothuyk')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (CAST(N'1997-12-12' AS Date), 340, 1, 3, 22, N'78 Nguyễn Văn Cừ, Quận Long Biên, Hà Nội', N'vutienl@gmail.com', N'Vũ Tiến Long', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0912345684', NULL, N'vutienl')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (CAST(N'2003-04-18' AS Date), 10, 1, 3, 23, N'99 Võ Văn Tần, Quận 3, TP.HCM', N'buihoaim@gmail.com', N'Bùi Hoài Nam', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0912345685', NULL, N'buihoaim')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (CAST(N'2001-08-25' AS Date), 0, 0, 3, 24, N'101 Bà Triệu, Quận Hai Bà Trưng, Hà Nội', N'ngothanhn@gmail.com', N'Ngô Thanh Ngân', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0912345686', NULL, N'ngothanhn')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (CAST(N'1996-06-06' AS Date), 99.5, 1, 3, 25, N'22 Lê Duẩn, Quận 1, TP.HCM', N'dinhcongo@gmail.com', N'Đinh Công Oanh', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0912345687', NULL, N'dinhcongo')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (CAST(N'1999-02-14' AS Date), 250, 1, 3, 26, N'44 Tôn Đức Thắng, Quận Đống Đa, Hà Nội', N'maivanthp@gmail.com', N'Mai Văn Phúc', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0912345688', NULL, N'maivanthp')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (CAST(N'1998-10-30' AS Date), 55, 1, 3, 27, N'67 Nguyễn Thị Minh Khai, Quận 3, TP.HCM', N'trinhmyq@gmail.com', N'Trịnh Mỹ Quyên', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0912345689', NULL, N'trinhmyq')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (CAST(N'1997-07-07' AS Date), 780.75, 1, 3, 28, N'88 Điện Biên Phủ, Quận Bình Thạnh, TP.HCM', N'lyminht@gmail.com', N'Lý Minh Tâm', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0912345690', NULL, N'lyminht')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (CAST(N'2002-05-09' AS Date), 12, 1, 3, 29, N'12 Quang Trung, Quận Gò Vấp, TP.HCM', N'caovany@gmail.com', N'Cao Văn Ý', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0912345691', NULL, N'caovany')
GO
INSERT [dbo].[Users] ([Birthday], [Coin], [Status], [RoleID], [UserID], [Address], [Email], [FullName], [Password], [PersonalSkill], [Phone], [ProfilePicture], [Username]) VALUES (CAST(N'2000-03-08' AS Date), 45, 1, 3, 30, N'34 Pasteur, Quận 1, TP.HCM', N'huynhngocu@gmail.com', N'Huỳnh Ngọc Uyên', N'$2a$10$wiI9lpFK3TnnQGSMc9RI3OuWsBeb.vx8CO.5car5kYEokfrfa7tbu', NULL, N'0912345692', NULL, N'huynhngocu')
GO
SET IDENTITY_INSERT [dbo].[Users] OFF
GO

-- =============================================
-- BƯỚC 3: CHÈN DỮ LIỆU CẤU TRÚC KHÓA HỌC (COURSES, CHAPTERS, LESSONS, ...)
-- =============================================

-- Bảng: Courses (Phụ thuộc vào Users, Categories)
SET IDENTITY_INSERT [dbo].[Courses] ON
GO
INSERT [dbo].[Courses] ([CanResubmit], [Discount], [IsFree], [Price], [CategoryID], [CourseID], [CreatedAt], [InstructorID], [UpdatedAt], [CourseImg], [CourseLevel], [Description], [Status], [Title], [VideoUrlPreview]) VALUES (NULL, NULL, 0, 1200000, 3, 1, GETDATE(), 8, GETDATE(), N'https://res.cloudinary.com/dmwo4yrsq/image/upload/8ae26e68-072f-42c4-9801-a6dd60f6d360_python-8665904_640.png?_a=DAGAACAVZAA0', N'Beginner', N'python, ngon ngu toan cau', N'approved', N'Python for beginer', N'https://res.cloudinary.com/dmwo4yrsq/video/upload/956c621c-526f-4dcb-92dd-b6b961d9e46c_136274-764387706_tiny.mp4?_a=DAGAACAVZAA0')
GO
INSERT [dbo].[Courses] ([CanResubmit], [Discount], [IsFree], [Price], [CategoryID], [CourseID], [CreatedAt], [InstructorID], [UpdatedAt], [CourseImg], [CourseLevel], [Description], [Status], [Title], [VideoUrlPreview]) VALUES (NULL, NULL, 0, 120000, 1, 2, GETDATE(), 6, GETDATE(), N'https://res.cloudinary.com/dmwo4yrsq/image/upload/1dd5fe4f-06fc-4c97-ad7e-1992405751e1_javascript-736400_640.png?_a=DAGAACAVZAA0', N'Advance', N'A complete course to help you master JavaScript...', N'approved', N'The Complete JavaScript Course 2025: From Zero to Expert!', N'https://res.cloudinary.com/dmwo4yrsq/video/upload/b7f1b831-d713-40c9-8e13-d4053cfecb2f_136274-764387706_tiny.mp4?_a=DAGAACAVZAA0')
GO
SET IDENTITY_INSERT [dbo].[Courses] OFF
GO

-- Bảng: Chapters (Phụ thuộc vào Courses)
SET IDENTITY_INSERT [dbo].[Chapters] ON
GO
INSERT [dbo].[Chapters] ([OrderNumber], [ChapterID], [CourseID], [CreatedAt], [UpdatedAt], [Description], [Title]) VALUES (1, 0, 1, GETDATE(), GETDATE(), N'welcome to python', N'preview to python in the world')
GO
INSERT [dbo].[Chapters] ([OrderNumber], [ChapterID], [CourseID], [CreatedAt], [UpdatedAt], [Description], [Title]) VALUES (2, 1, 1, GETDATE(), GETDATE(), N'syntax, variable, function, ...', N'basic python')
GO
INSERT [dbo].[Chapters] ([OrderNumber], [ChapterID], [CourseID], [CreatedAt], [UpdatedAt], [Description], [Title]) VALUES (1, 2, 2, GETDATE(), GETDATE(), N'intro to java script programming', N'Intro Java Script Pro')
GO
INSERT [dbo].[Chapters] ([OrderNumber], [ChapterID], [CourseID], [CreatedAt], [UpdatedAt], [Description], [Title]) VALUES (2, 3, 2, GETDATE(), GETDATE(), N'learning java script', N'learning programming')
GO
INSERT [dbo].[Chapters] ([OrderNumber], [ChapterID], [CourseID], [CreatedAt], [UpdatedAt], [Description], [Title]) VALUES (3, 4, 2, GETDATE(), GETDATE(), N'Conclusion and practice test', N'Outro')
GO
SET IDENTITY_INSERT [dbo].[Chapters] OFF
GO

-- Bảng: Lessons (Phụ thuộc vào Chapters)
SET IDENTITY_INSERT [dbo].[Lessons] ON
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (720, 1, 1, 0, GETDATE(), 0, GETDATE(), N'video', N'gioi thieu co ban ve khoa hoc', N'intro')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (600, 0, 2, 0, GETDATE(), 1, GETDATE(), N'quiz', N'python', N'python with develops')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (720, 0, 1, 1, GETDATE(), 2, GETDATE(), N'video', N'cai dat moi truong', N'cai dat moi truong lap trinh')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (840, 0, 2, 1, GETDATE(), 3, GETDATE(), N'video', N'file python', N'chay file python dau tien')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (900, 0, 3, 1, GETDATE(), 4, GETDATE(), N'video', N'comment', N'comment trong python')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (900, 0, 4, 1, GETDATE(), 5, GETDATE(), N'video', N'variable', N'variable')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (1500, 0, 5, 1, GETDATE(), 6, GETDATE(), N'video', N'kieu so ', N'kieu so trong python')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (1800, 0, 6, 1, GETDATE(), 7, GETDATE(), N'quiz', N'test', N'Quiz test chapter 2')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (1800, 1, 1, 2, GETDATE(), 8, GETDATE(), N'video', N'Learn how to set up your development environment step-by-step.', N'install envirollment and intro course')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (120, 0, 1, 3, GETDATE(), 9, GETDATE(), N'video', N'Understand the difference between primitive and reference types in JavaScript.', N'Primitive Types & Reference Types in Java Script')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (780, 1, 2, 3, GETDATE(), 10, GETDATE(), N'video', N'Explore how JavaScript handles data when passing variables to functions.', N'pass by value and pass by reference ')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (1440, 1, 3, 3, GETDATE(), 11, GETDATE(), N'video', N'Learn what an IIFE is and why it’s used in JavaScript.', N'IIFE in Java Script')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (600, 1, 4, 3, GETDATE(), 12, GETDATE(), N'video', N'Understand how JavaScript hoists variable and function declarations.', N'Hosting in JavaScript')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (1260, 1, 5, 3, GETDATE(), 13, GETDATE(), N'video', N'Learn how the call() method allows you to invoke functions with a specific this context.', N'Fn.call() method in Java Script')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (900, 1, 6, 3, GETDATE(), 14, GETDATE(), N'video', N'Discover how the bind() method creates a new function with a fixed this context.', N'Fn.blind() method in JavaScript')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (2400, 1, 7, 3, GETDATE(), 15, GETDATE(), N'video', N'Understand closures a powerful feature in JavaScript that allows inner.', N'Closure in JavaScript')
GO
INSERT [dbo].[Lessons] ([Duration], [IsFree], [OrderNumber], [ChapterID], [CreatedAt], [LessonID], [UpdatedAt], [ContentType], [Description], [Title]) VALUES (3600, 1, 1, 4, GETDATE(), 16, GETDATE(), N'quiz', N'test demo after completed course', N'test exam ')
GO
SET IDENTITY_INSERT [dbo].[Lessons] OFF
GO

-- Bảng: Video (Phụ thuộc vào Lessons)
SET IDENTITY_INSERT [dbo].[Video] ON
GO
INSERT [dbo].[Video] ([Duration], [LessonID], [UploadDate], [VideoID], [VideoURL]) VALUES (720, 0, GETDATE(), 0, N'https://res.cloudinary.com/dmwo4yrsq/video/upload/4859891f-fa12-4d03-947f-626806d7abca_%5BKh%C3%B3a%20h%E1%BB%8Dc%20l%E1%BA%ADp%20tr%C3%ACnh%20Python%20c%C6%A1%20b%E1%BA%A3n%5D%20-%20B%C3%A0i%201_%20Gi%E1%BB%9Bi%20thi%E1%BB%87u%20ng%C3%B4n%20ng%E1%BB%AF%20l%E1%BA%ADp%20tr%C3%ACnh%20Python%20_%20HowKteam.mp4?_a=DAGAACAVZAA0')
GO
INSERT [dbo].[Video] ([Duration], [LessonID], [UploadDate], [VideoID], [VideoURL]) VALUES (720, 2, GETDATE(), 1, N'https://res.cloudinary.com/dmwo4yrsq/video/upload/9db61b5d-053c-4c97-a4a6-fccfd004be68_%5BKh%C3%B3a%20h%E1%BB%8Dc%20l%E1%BA%ADp%20tr%C3%ACnh%20Python%20c%C6%A1%20b%E1%BA%A3n%5D%20-%20B%C3%A0i%202_%20C%C3%A0i%20%C4%91%E1%BA%B7t%20m%C3%B4i%20tr%C6%B0%E1%BB%9Dng%20Python%20_%20HowKteam.mp4?_a=DAGAACAVZAA0')
GO
INSERT [dbo].[Video] ([Duration], [LessonID], [UploadDate], [VideoID], [VideoURL]) VALUES (840, 3, GETDATE(), 2, N'https://res.cloudinary.com/dmwo4yrsq/video/upload/c7713f2d-64ef-4f00-883e-dd784e54583b_%5BKh%C3%B3a%20h%E1%BB%8Dc%20l%E1%BA%ADp%20tr%C3%ACnh%20Python%20c%C6%A1%20b%E1%BA%A3n%5D%20-%20B%C3%A0i%203_%20Ch%E1%BA%A1y%20file%20Python%20_%20HowKteam.mp4?_a=DAGAACAVZAA0')
GO
INSERT [dbo].[Video] ([Duration], [LessonID], [UploadDate], [VideoID], [VideoURL]) VALUES (900, 4, GETDATE(), 3, N'https://res.cloudinary.com/dmwo4yrsq/video/upload/d02595a2-70c1-4a01-9ea1-e1d50c1f6c97_%5BKh%C3%B3a%20h%E1%BB%8Dc%20l%E1%BA%ADp%20tr%C3%ACnh%20Python%20c%C6%A1%20b%E1%BA%A3n%5D%20-%20B%C3%A0i%204_%20Comment%20trong%20Python%20_%20HowKteam.mp4?_a=DAGAACAVZAA0')
GO
INSERT [dbo].[Video] ([Duration], [LessonID], [UploadDate], [VideoID], [VideoURL]) VALUES (900, 5, GETDATE(), 4, N'https://res.cloudinary.com/dmwo4yrsq/video/upload/8940d5a0-976c-4f4f-a111-dc3a4397c6fc_%5BKh%C3%B3a%20h%E1%BB%8Dc%20l%E1%BA%ADp%20tr%C3%ACnh%20Python%20c%C6%A1%20b%E1%BA%A3n%5D%20-%20B%C3%A0i%205_%20Bi%E1%BA%BFn%28Variable%29%20trong%20Python%20_%20HowKteam.mp4?_a=DAGAACAVZAA0')
GO
INSERT [dbo].[Video] ([Duration], [LessonID], [UploadDate], [VideoID], [VideoURL]) VALUES (1500, 6, GETDATE(), 5, N'https://res.cloudinary.com/dmwo4yrsq/video/upload/cd197a6e-c513-4ef1-b22b-1a8afe785752_%5BKh%C3%B3a%20h%E1%BB%8Dc%20l%E1%BA%ADp%20tr%C3%ACnh%20Python%20c%C6%A1%20b%E1%BA%A3n%5D%20-%20B%C3%A0i%206_%20Ki%E1%BB%83u%20s%E1%BB%91%20trong%20Python%20_%20HowKteam.mp4?_a=DAGAACAVZAA0')
GO
INSERT [dbo].[Video] ([Duration], [LessonID], [UploadDate], [VideoID], [VideoURL]) VALUES (1800, 8, GETDATE(), 6, N'https://res.cloudinary.com/dmwo4yrsq/video/upload/601d1c6e-c117-4ec5-83b6-dd752a6dbf62_Ra%20m%E1%BA%AFt%20kh%C3%B3a%20JavaScript%20n%C3%A2ng%20cao%20t%E1%BA%A1i%20F8%20_%20JavaScript%20Advanced.mp4?_a=DAGAACAVZAA0')
GO
INSERT [dbo].[Video] ([Duration], [LessonID], [UploadDate], [VideoID], [VideoURL]) VALUES (120, 9, GETDATE(), 7, N'https://res.cloudinary.com/dmwo4yrsq/video/upload/132ceb64-d927-4686-a9ab-1a0a21f3aee3_Ra%20m%E1%BA%AFt%20kh%C3%B3a%20JavaScript%20n%C3%A2ng%20cao%20t%E1%BA%A1i%20F8%20_%20JavaScript%20Advanced.mp4?_a=DAGAACAVZAA0')
GO
INSERT [dbo].[Video] ([Duration], [LessonID], [UploadDate], [VideoID], [VideoURL]) VALUES (780, 10, GETDATE(), 8, N'https://res.cloudinary.com/dmwo4yrsq/video/upload/5ce0a95a-0337-44a3-a4be-9dcf0f64619c_Pass%20by%20value%20v%C3%A0%20Pass%20by%20reference%20l%C3%A0%20g%C3%AC_%20_%20Tham%20tr%E1%BB%8B%20v%C3%A0%20tham%20chi%E1%BA%BFu%20l%C3%A0%20g%C3%AC_%20_%20JavaScript%20Pro%20by%20F8.mp4?_a=DAGAACAVZAA0')
GO
INSERT [dbo].[Video] ([Duration], [LessonID], [UploadDate], [VideoID], [VideoURL]) VALUES (1440, 11, GETDATE(), 9, N'https://res.cloudinary.com/dmwo4yrsq/video/upload/e8cdaac5-fc7b-463f-a1c4-0548a961906b_Kh%C3%A1i%20ni%E1%BB%87m%20IIFE%20trong%20JavaScript%20_%20JavaScript%20N%C3%A2ng%20Cao.mp4?_a=DAGAACAVZAA0')
GO
INSERT [dbo].[Video] ([Duration], [LessonID], [UploadDate], [VideoID], [VideoURL]) VALUES (600, 12, GETDATE(), 10, N'https://res.cloudinary.com/dmwo4yrsq/video/upload/0fa0c5dd-0757-4e09-979c-0cb8a5c6c895_Hoisting%20trong%20Javascript%20_%20JavaScript%20n%C3%A2ng%20cao.mp4?_a=DAGAACAVZAA0')
GO
INSERT [dbo].[Video] ([Duration], [LessonID], [UploadDate], [VideoID], [VideoURL]) VALUES (1260, 13, GETDATE(), 11, N'https://res.cloudinary.com/dmwo4yrsq/video/upload/2c9d0b8c-700e-4f62-a621-13a722aa667f_Fn.call%28%29%20method%20trong%20JavaScript%20_%20JavaScript%20n%C3%A2ng%20cao?_a=DAGAACAVZAA0')
GO
INSERT [dbo].[Video] ([Duration], [LessonID], [UploadDate], [VideoID], [VideoURL]) VALUES (900, 14, GETDATE(), 12, N'https://res.cloudinary.com/dmwo4yrsq/video/upload/2993a957-f39a-4f38-8bbd-5d74bce804a6_Fn.bind%28%29%20method%20trong%20JavaScript%20-%20ph%E1%BA%A7n%201%20_%20JavaScript%20n%C3%A2ng%20cao?_a=DAGAACAVZAA0')
GO
INSERT [dbo].[Video] ([Duration], [LessonID], [UploadDate], [VideoID], [VideoURL]) VALUES (2400, 15, GETDATE(), 13, N'https://res.cloudinary.com/dmwo4yrsq/video/upload/41d17421-70eb-4c61-b4c6-b334af3b73ec_Closure%20trong%20JavaScript%20_%20JavaScript%20n%C3%A2ng%20cao.mp4?_a=DAGAACAVZAA0')
GO
SET IDENTITY_INSERT [dbo].[Video] OFF
GO

-- Bảng: Quiz_test (Phụ thuộc vào Lessons)
SET IDENTITY_INSERT [dbo].[Quiz_test] ON
GO
INSERT [dbo].[Quiz_test] ([TimeLimit], [CreatedAt], [LessonID], [QuizID], [UpdatedAt], [Description], [Title]) VALUES (10, GETDATE(), 1, 0, GETDATE(), N'test do hieu biet kien thuc', N'test lap trinh co ban')
GO
INSERT [dbo].[Quiz_test] ([TimeLimit], [CreatedAt], [LessonID], [QuizID], [UpdatedAt], [Description], [Title]) VALUES (30, GETDATE(), 7, 1, GETDATE(), N'test', N'test chapter 2')
GO
INSERT [dbo].[Quiz_test] ([TimeLimit], [CreatedAt], [LessonID], [QuizID], [UpdatedAt], [Description], [Title]) VALUES (60, GETDATE(), 16, 2, GETDATE(), N'test', N'Test Exam ')
GO
SET IDENTITY_INSERT [dbo].[Quiz_test] OFF
GO

-- Bảng: Quiz_questions (Phụ thuộc vào Quiz_test)
SET IDENTITY_INSERT [dbo].[Quiz_questions] ON
GO
INSERT [dbo].[Quiz_questions] ([OrderNumber], [QuestionID], [QuizID], [CorrectAnswer], [OptionA], [OptionB], [OptionC], [OptionD], [Question]) VALUES (1, 0, 0, N'B', N'la ngon ngu lap trinh', N'la thu tao len chuong trinh', N'la con bo', N'la con lon', N'ngon ngu lap trinh la gi')
GO
INSERT [dbo].[Quiz_questions] ([OrderNumber], [QuestionID], [QuizID], [CorrectAnswer], [OptionA], [OptionB], [OptionC], [OptionD], [Question]) VALUES (2, 1, 0, N'D', N'18', N'10', N'12', N'khong dem duoc', N'co bao nhieu ngon ngu lap trinh')
GO
INSERT [dbo].[Quiz_questions] ([OrderNumber], [QuestionID], [QuizID], [CorrectAnswer], [OptionA], [OptionB], [OptionC], [OptionD], [Question]) VALUES (1, 2, 1, N'B', N'Assembly', N'Object-Oriented', N'Compiled', N'Machine language', N'What type of programming language is Python?')
GO
INSERT [dbo].[Quiz_questions] ([OrderNumber], [QuestionID], [QuizID], [CorrectAnswer], [OptionA], [OptionB], [OptionC], [OptionD], [Question]) VALUES (2, 3, 1, N'C', N'27', N'9', N'3', N'1', N'What is the result of 3 * 1 ** 3?')
GO
INSERT [dbo].[Quiz_questions] ([OrderNumber], [QuestionID], [QuizID], [CorrectAnswer], [OptionA], [OptionB], [OptionC], [OptionD], [Question]) VALUES (3, 4, 1, N'D', N'List', N'Dictionary', N'Set', N'Tuple', N'Which of the following is an immutable data type in Python?')
GO
INSERT [dbo].[Quiz_questions] ([OrderNumber], [QuestionID], [QuizID], [CorrectAnswer], [OptionA], [OptionB], [OptionC], [OptionD], [Question]) VALUES (1, 5, 2, N'B', N'"null"', N'"object" ', N'"undefined"', N'"boolean"', N'What is the output of the following code?')
GO
INSERT [dbo].[Quiz_questions] ([OrderNumber], [QuestionID], [QuizID], [CorrectAnswer], [OptionA], [OptionB], [OptionC], [OptionD], [Question]) VALUES (2, 6, 2, N'C', N'String', N'Number', N'Object ', N'Boolean', N'Which of the following is NOT a primitive type in JavaScript?')
GO
INSERT [dbo].[Quiz_questions] ([OrderNumber], [QuestionID], [QuizID], [CorrectAnswer], [OptionA], [OptionB], [OptionC], [OptionD], [Question]) VALUES (4, 7, 2, N'B', N'Moving CSS to the top', N'Moving variables/functions to the top of scope', N'Memory leak', N'Performance optimization', N'What does "hoisting" refer to in JavaScript?')
GO
SET IDENTITY_INSERT [dbo].[Quiz_questions] OFF
GO


-- =============================================
-- BƯỚC 4: CHÈN DỮ LIỆU GIAO DỊCH, ĐƠN HÀNG, GHI DANH
-- =============================================

-- Bảng: Orders (Phụ thuộc vào Users)
SET IDENTITY_INSERT [dbo].[Orders] ON
GO
INSERT [dbo].[Orders] ([Amount], [OrderDate], [OrderID], [UserID], [OrderType], [RefCode], [Status]) VALUES (1320000, CAST(N'2025-04-13T00:00:00.000' AS DateTime), 29, 16, N'course_purchase', NULL, N'completed')
GO
INSERT [dbo].[Orders] ([Amount], [OrderDate], [OrderID], [UserID], [OrderType], [RefCode], [Status]) VALUES (1320000, CAST(N'2025-05-05T00:00:00.000' AS DateTime), 30, 17, N'course_purchase', NULL, N'completed')
GO
INSERT [dbo].[Orders] ([Amount], [OrderDate], [OrderID], [UserID], [OrderType], [RefCode], [Status]) VALUES (1320000, CAST(N'2025-06-10T00:00:00.000' AS DateTime), 31, 18, N'course_purchase', NULL, N'completed')
GO
INSERT [dbo].[Orders] ([Amount], [OrderDate], [OrderID], [UserID], [OrderType], [RefCode], [Status]) VALUES (1320000, CAST(N'2025-06-17T00:00:00.000' AS DateTime), 32, 19, N'course_purchase', NULL, N'completed')
GO
INSERT [dbo].[Orders] ([Amount], [OrderDate], [OrderID], [UserID], [OrderType], [RefCode], [Status]) VALUES (1320000, CAST(N'2025-06-25T00:00:00.000' AS DateTime), 33, 20, N'course_purchase', NULL, N'completed')
GO
INSERT [dbo].[Orders] ([Amount], [OrderDate], [OrderID], [UserID], [OrderType], [RefCode], [Status]) VALUES (1320000, CAST(N'2025-04-26T00:00:00.000' AS DateTime), 34, 21, N'course_purchase', NULL, N'completed')
GO
INSERT [dbo].[Orders] ([Amount], [OrderDate], [OrderID], [UserID], [OrderType], [RefCode], [Status]) VALUES (1320000, CAST(N'2025-04-28T00:00:00.000' AS DateTime), 35, 22, N'course_purchase', NULL, N'completed')
GO
INSERT [dbo].[Orders] ([Amount], [OrderDate], [OrderID], [UserID], [OrderType], [RefCode], [Status]) VALUES (1320000, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 36, 23, N'course_purchase', NULL, N'completed')
GO
INSERT [dbo].[Orders] ([Amount], [OrderDate], [OrderID], [UserID], [OrderType], [RefCode], [Status]) VALUES (1320000, CAST(N'2025-05-16T00:00:00.000' AS DateTime), 37, 25, N'course_purchase', NULL, N'completed')
GO
INSERT [dbo].[Orders] ([Amount], [OrderDate], [OrderID], [UserID], [OrderType], [RefCode], [Status]) VALUES (1320000, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 38, 26, N'course_purchase', NULL, N'completed')
GO
INSERT [dbo].[Orders] ([Amount], [OrderDate], [OrderID], [UserID], [OrderType], [RefCode], [Status]) VALUES (1320000, CAST(N'2025-05-14T00:00:00.000' AS DateTime), 39, 27, N'course_purchase', NULL, N'completed')
GO
INSERT [dbo].[Orders] ([Amount], [OrderDate], [OrderID], [UserID], [OrderType], [RefCode], [Status]) VALUES (1320000, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 40, 28, N'course_purchase', NULL, N'completed')
GO
INSERT [dbo].[Orders] ([Amount], [OrderDate], [OrderID], [UserID], [OrderType], [RefCode], [Status]) VALUES (1320000, CAST(N'2025-06-03T00:00:00.000' AS DateTime), 41, 29, N'course_purchase', NULL, N'completed')
GO
INSERT [dbo].[Orders] ([Amount], [OrderDate], [OrderID], [UserID], [OrderType], [RefCode], [Status]) VALUES (1320000, CAST(N'2025-05-10T00:00:00.000' AS DateTime), 42, 30, N'course_purchase', NULL, N'completed')
GO
SET IDENTITY_INSERT [dbo].[Orders] OFF
GO

-- Bảng: OrderDetail (Phụ thuộc vào Orders, Courses)
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (1200000, 1, 29)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (120000, 2, 29)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (1200000, 1, 30)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (120000, 2, 30)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (1200000, 1, 31)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (120000, 2, 31)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (1200000, 1, 32)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (120000, 2, 32)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (1200000, 1, 33)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (120000, 2, 33)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (1200000, 1, 34)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (120000, 2, 34)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (1200000, 1, 35)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (120000, 2, 35)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (1200000, 1, 36)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (120000, 2, 36)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (1200000, 1, 37)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (120000, 2, 37)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (1200000, 1, 38)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (120000, 2, 38)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (1200000, 1, 39)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (120000, 2, 39)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (1200000, 1, 40)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (120000, 2, 40)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (1200000, 1, 41)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (120000, 2, 41)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (1200000, 1, 42)
GO
INSERT [dbo].[OrderDetail] ([UnitPrice], [CourseID], [OrderID]) VALUES (120000, 2, 42)
GO

-- Bảng: Enrollments (Phụ thuộc vào Users, Courses, Orders)
SET IDENTITY_INSERT [dbo].[Enrollments] ON
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (57, 0, 1, CAST(N'2025-04-13T00:00:00.000' AS DateTime), 29, 16, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (58, 0, 1, CAST(N'2025-05-05T00:00:00.000' AS DateTime), 30, 17, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (59, 0, 1, CAST(N'2025-06-10T00:00:00.000' AS DateTime), 31, 18, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (60, 0, 1, CAST(N'2025-06-17T00:00:00.000' AS DateTime), 32, 19, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (61, 0, 1, CAST(N'2025-06-25T00:00:00.000' AS DateTime), 33, 20, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (62, 0, 1, CAST(N'2025-04-26T00:00:00.000' AS DateTime), 34, 21, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (63, 0, 1, CAST(N'2025-04-28T00:00:00.000' AS DateTime), 35, 22, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (64, 0, 1, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 36, 23, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (65, 0, 1, CAST(N'2025-05-16T00:00:00.000' AS DateTime), 37, 25, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (66, 0, 1, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 38, 26, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (67, 0, 1, CAST(N'2025-05-14T00:00:00.000' AS DateTime), 39, 27, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (68, 0, 1, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 40, 28, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (69, 0, 1, CAST(N'2025-06-03T00:00:00.000' AS DateTime), 41, 29, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (70, 0, 1, CAST(N'2025-05-10T00:00:00.000' AS DateTime), 42, 30, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (71, 0, 2, CAST(N'2025-04-13T00:00:00.000' AS DateTime), 29, 16, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (72, 0, 2, CAST(N'2025-05-05T00:00:00.000' AS DateTime), 30, 17, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (73, 0, 2, CAST(N'2025-06-10T00:00:00.000' AS DateTime), 31, 18, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (74, 0, 2, CAST(N'2025-06-17T00:00:00.000' AS DateTime), 32, 19, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (75, 0, 2, CAST(N'2025-06-25T00:00:00.000' AS DateTime), 33, 20, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (76, 0, 2, CAST(N'2025-04-26T00:00:00.000' AS DateTime), 34, 21, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (77, 0, 2, CAST(N'2025-04-28T00:00:00.000' AS DateTime), 35, 22, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (78, 0, 2, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 36, 23, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (79, 0, 2, CAST(N'2025-05-16T00:00:00.000' AS DateTime), 37, 25, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (80, 0, 2, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 38, 26, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (81, 0, 2, CAST(N'2025-05-14T00:00:00.000' AS DateTime), 39, 27, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (82, 0, 2, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 40, 28, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (83, 0, 2, CAST(N'2025-06-03T00:00:00.000' AS DateTime), 41, 29, N'onGoing')
GO
INSERT [dbo].[Enrollments] ([EnrollmentID], [Progress], [CourseID], [EnrollmentDate], [OrderID], [UserID], [Status]) VALUES (84, 0, 2, CAST(N'2025-05-10T00:00:00.000' AS DateTime), 42, 30, N'onGoing')
GO
SET IDENTITY_INSERT [dbo].[Enrollments] OFF
GO

-- =============================================
-- BƯỚC 5: CHÈN DỮ LIỆU PHỤ THUỘC VÀO GHI DANH
-- =============================================

-- Bảng: CourseReviews (Phụ thuộc vào Courses, Enrollments)
SET IDENTITY_INSERT [dbo].[CourseReviews] ON
GO
INSERT [dbo].[CourseReviews] ([EnrollmentID], [Rating], [CourseID], [CreatedAt], [ReviewID], [UpdatedAt], [Comment]) VALUES (59, 4, 1, CAST(N'2025-06-10T00:00:00.000' AS DateTime), 29, NULL, N'Đây là một đánh giá được tạo tự động cho khóa học.')
GO
INSERT [dbo].[CourseReviews] ([EnrollmentID], [Rating], [CourseID], [CreatedAt], [ReviewID], [UpdatedAt], [Comment]) VALUES (60, 5, 1, CAST(N'2025-06-17T00:00:00.000' AS DateTime), 30, NULL, N'Đây là một đánh giá được tạo tự động cho khóa học.')
GO
INSERT [dbo].[CourseReviews] ([EnrollmentID], [Rating], [CourseID], [CreatedAt], [ReviewID], [UpdatedAt], [Comment]) VALUES (61, 1, 1, CAST(N'2025-06-25T00:00:00.000' AS DateTime), 31, NULL, N'Đây là một đánh giá được tạo tự động cho khóa học.')
GO
INSERT [dbo].[CourseReviews] ([EnrollmentID], [Rating], [CourseID], [CreatedAt], [ReviewID], [UpdatedAt], [Comment]) VALUES (63, 3, 1, CAST(N'2025-04-28T00:00:00.000' AS DateTime), 32, NULL, N'Đây là một đánh giá được tạo tự động cho khóa học.')
GO
INSERT [dbo].[CourseReviews] ([EnrollmentID], [Rating], [CourseID], [CreatedAt], [ReviewID], [UpdatedAt], [Comment]) VALUES (64, 4, 1, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 33, NULL, N'Đây là một đánh giá được tạo tự động cho khóa học.')
GO
INSERT [dbo].[CourseReviews] ([EnrollmentID], [Rating], [CourseID], [CreatedAt], [ReviewID], [UpdatedAt], [Comment]) VALUES (65, 1, 1, CAST(N'2025-05-16T00:00:00.000' AS DateTime), 34, NULL, N'Đây là một đánh giá được tạo tự động cho khóa học.')
GO
INSERT [dbo].[CourseReviews] ([EnrollmentID], [Rating], [CourseID], [CreatedAt], [ReviewID], [UpdatedAt], [Comment]) VALUES (66, 2, 1, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 35, NULL, N'Đây là một đánh giá được tạo tự động cho khóa học.')
GO
INSERT [dbo].[CourseReviews] ([EnrollmentID], [Rating], [CourseID], [CreatedAt], [ReviewID], [UpdatedAt], [Comment]) VALUES (69, 5, 1, CAST(N'2025-06-03T00:00:00.000' AS DateTime), 36, NULL, N'Đây là một đánh giá được tạo tự động cho khóa học.')
GO
INSERT [dbo].[CourseReviews] ([EnrollmentID], [Rating], [CourseID], [CreatedAt], [ReviewID], [UpdatedAt], [Comment]) VALUES (70, 1, 1, CAST(N'2025-05-10T00:00:00.000' AS DateTime), 37, NULL, N'Đây là một đánh giá được tạo tự động cho khóa học.')
GO
INSERT [dbo].[CourseReviews] ([EnrollmentID], [Rating], [CourseID], [CreatedAt], [ReviewID], [UpdatedAt], [Comment]) VALUES (71, 2, 2, CAST(N'2025-04-13T00:00:00.000' AS DateTime), 38, NULL, N'Đây là một đánh giá được tạo tự động cho khóa học.')
GO
INSERT [dbo].[CourseReviews] ([EnrollmentID], [Rating], [CourseID], [CreatedAt], [ReviewID], [UpdatedAt], [Comment]) VALUES (72, 3, 2, CAST(N'2025-05-05T00:00:00.000' AS DateTime), 39, NULL, N'Đây là một đánh giá được tạo tự động cho khóa học.')
GO
INSERT [dbo].[CourseReviews] ([EnrollmentID], [Rating], [CourseID], [CreatedAt], [ReviewID], [UpdatedAt], [Comment]) VALUES (76, 2, 2, CAST(N'2025-04-26T00:00:00.000' AS DateTime), 40, NULL, N'Đây là một đánh giá được tạo tự động cho khóa học.')
GO
INSERT [dbo].[CourseReviews] ([EnrollmentID], [Rating], [CourseID], [CreatedAt], [ReviewID], [UpdatedAt], [Comment]) VALUES (81, 3, 2, CAST(N'2025-05-14T00:00:00.000' AS DateTime), 41, NULL, N'Đây là một đánh giá được tạo tự động cho khóa học.')
GO
INSERT [dbo].[CourseReviews] ([EnrollmentID], [Rating], [CourseID], [CreatedAt], [ReviewID], [UpdatedAt], [Comment]) VALUES (82, 4, 2, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 42, NULL, N'Đây là một đánh giá được tạo tự động cho khóa học.')
GO
SET IDENTITY_INSERT [dbo].[CourseReviews] OFF
GO

-- =============================================
-- BƯỚC 6: CHÈN DỮ LIỆU GIAO DỊCH TIỀN TỆ
-- =============================================

-- Bảng: CoinTransaction (Phụ thuộc vào Users)
SET IDENTITY_INSERT [dbo].[CoinTransaction] ON
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (1320000.00, CAST(N'2025-04-13T00:00:00.000' AS DateTime), 71, 16, N'Hệ thống tự động nạp tiền để mua tất cả khóa học', N'completed', N'top_up')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (1320000.00, CAST(N'2025-05-05T00:00:00.000' AS DateTime), 72, 17, N'Hệ thống tự động nạp tiền để mua tất cả khóa học', N'completed', N'top_up')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (1320000.00, CAST(N'2025-06-10T00:00:00.000' AS DateTime), 73, 18, N'Hệ thống tự động nạp tiền để mua tất cả khóa học', N'completed', N'top_up')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (1320000.00, CAST(N'2025-06-17T00:00:00.000' AS DateTime), 74, 19, N'Hệ thống tự động nạp tiền để mua tất cả khóa học', N'completed', N'top_up')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (1320000.00, CAST(N'2025-06-25T00:00:00.000' AS DateTime), 75, 20, N'Hệ thống tự động nạp tiền để mua tất cả khóa học', N'completed', N'top_up')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (1320000.00, CAST(N'2025-04-26T00:00:00.000' AS DateTime), 76, 21, N'Hệ thống tự động nạp tiền để mua tất cả khóa học', N'completed', N'top_up')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (1320000.00, CAST(N'2025-04-28T00:00:00.000' AS DateTime), 77, 22, N'Hệ thống tự động nạp tiền để mua tất cả khóa học', N'completed', N'top_up')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (1320000.00, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 78, 23, N'Hệ thống tự động nạp tiền để mua tất cả khóa học', N'completed', N'top_up')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (1320000.00, CAST(N'2025-05-16T00:00:00.000' AS DateTime), 79, 25, N'Hệ thống tự động nạp tiền để mua tất cả khóa học', N'completed', N'top_up')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (1320000.00, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 80, 26, N'Hệ thống tự động nạp tiền để mua tất cả khóa học', N'completed', N'top_up')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (1320000.00, CAST(N'2025-05-14T00:00:00.000' AS DateTime), 81, 27, N'Hệ thống tự động nạp tiền để mua tất cả khóa học', N'completed', N'top_up')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (1320000.00, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 82, 28, N'Hệ thống tự động nạp tiền để mua tất cả khóa học', N'completed', N'top_up')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (1320000.00, CAST(N'2025-06-03T00:00:00.000' AS DateTime), 83, 29, N'Hệ thống tự động nạp tiền để mua tất cả khóa học', N'completed', N'top_up')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (1320000.00, CAST(N'2025-05-10T00:00:00.000' AS DateTime), 84, 30, N'Hệ thống tự động nạp tiền để mua tất cả khóa học', N'completed', N'top_up')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (-1320000.00, CAST(N'2025-04-13T00:00:00.000' AS DateTime), 85, 16, N'Hệ thống tự động mua tất cả khóa học', N'completed', N'course_purchase')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (-1320000.00, CAST(N'2025-05-05T00:00:00.000' AS DateTime), 86, 17, N'Hệ thống tự động mua tất cả khóa học', N'completed', N'course_purchase')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (-1320000.00, CAST(N'2025-06-10T00:00:00.000' AS DateTime), 87, 18, N'Hệ thống tự động mua tất cả khóa học', N'completed', N'course_purchase')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (-1320000.00, CAST(N'2025-06-17T00:00:00.000' AS DateTime), 88, 19, N'Hệ thống tự động mua tất cả khóa học', N'completed', N'course_purchase')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (-1320000.00, CAST(N'2025-06-25T00:00:00.000' AS DateTime), 89, 20, N'Hệ thống tự động mua tất cả khóa học', N'completed', N'course_purchase')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (-1320000.00, CAST(N'2025-04-26T00:00:00.000' AS DateTime), 90, 21, N'Hệ thống tự động mua tất cả khóa học', N'completed', N'course_purchase')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (-1320000.00, CAST(N'2025-04-28T00:00:00.000' AS DateTime), 91, 22, N'Hệ thống tự động mua tất cả khóa học', N'completed', N'course_purchase')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (-1320000.00, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 92, 23, N'Hệ thống tự động mua tất cả khóa học', N'completed', N'course_purchase')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (-1320000.00, CAST(N'2025-05-16T00:00:00.000' AS DateTime), 93, 25, N'Hệ thống tự động mua tất cả khóa học', N'completed', N'course_purchase')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (-1320000.00, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 94, 26, N'Hệ thống tự động mua tất cả khóa học', N'completed', N'course_purchase')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (-1320000.00, CAST(N'2025-05-14T00:00:00.000' AS DateTime), 95, 27, N'Hệ thống tự động mua tất cả khóa học', N'completed', N'course_purchase')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (-1320000.00, CAST(N'2025-05-17T00:00:00.000' AS DateTime), 96, 28, N'Hệ thống tự động mua tất cả khóa học', N'completed', N'course_purchase')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (-1320000.00, CAST(N'2025-06-03T00:00:00.000' AS DateTime), 97, 29, N'Hệ thống tự động mua tất cả khóa học', N'completed', N'course_purchase')
GO
INSERT [dbo].[CoinTransaction] ([Amount], [CreatedAt], [TransactionID], [UserID], [Note], [Status], [TransactionType]) VALUES (-1320000.00, CAST(N'2025-05-10T00:00:00.000' AS DateTime), 98, 30, N'Hệ thống tự động mua tất cả khóa học', N'completed', N'course_purchase')
GO
SET IDENTITY_INSERT [dbo].[CoinTransaction] OFF
GO