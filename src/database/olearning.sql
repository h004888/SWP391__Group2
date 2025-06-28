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
                       VideoURL NVARCHAR(MAX) NOT NULL,
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
                             FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
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
                      MinEnrollments INT NOT NULL,
                      MaxEnrollments INT NULL,
                      MaintenanceFee DECIMAL(10,2) NOT NULL,
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
                                   EnrollmentCount INT NOT NULL,
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
                               Message NVARCHAR(1000) NOT NULL,
                               SentAt DATETIME NOT NULL DEFAULT GETDATE(),
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
