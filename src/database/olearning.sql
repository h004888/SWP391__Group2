IF
NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'Olearning')
BEGIN
    CREATE
DATABASE Olearning;
END;
GO

USE Olearning;
GO
--update26/05
-- Bảng Roles
CREATE TABLE Roles
(
    RoleID SMALLINT IDENTITY(1,1) PRIMARY KEY,
    Name   NVARCHAR(20) NOT NULL UNIQUE
);
GO

-- Bảng Users
CREATE TABLE Users
(
    UserID         INT IDENTITY(1,1) PRIMARY KEY,
    Username       NVARCHAR(50) NOT NULL UNIQUE,
    RoleID         SMALLINT,
    Email          NVARCHAR(100) NOT NULL UNIQUE,
    Password       NVARCHAR(255) NOT NULL,
    FullName       NVARCHAR(100) NOT NULL,
    Phone          NVARCHAR(15),
    Birthday       DATETIME,
    Address        NVARCHAR(255),
    ProfilePicture NVARCHAR(255),
    PersonalSkills NVARCHAR(MAX),---new
    FOREIGN KEY (RoleID) REFERENCES Roles (RoleID)
);
GO

-- Bảng Categories
CREATE TABLE Categories
(
    CategoryID INT IDENTITY(1,1) PRIMARY KEY,
    Name       NVARCHAR(100) NOT NULL
);
GO

-- Bảng Courses
CREATE TABLE Courses
(
    CourseID            INT IDENTITY(1,1) PRIMARY KEY,
    UserID              INT            NOT NULL,
    CategoryID          INT,
    Title               NVARCHAR(255) NOT NULL,
    Description         NVARCHAR(2000),
    Price               DECIMAL(10, 2) NOT NULL DEFAULT 0,
    Discount            DECIMAL(5, 2)           DEFAULT 0,
    CourseImg           NVARCHAR(255),
    Duration            INT                     DEFAULT 0,
    TotalLessons        INT                     DEFAULT 0,
    TotalRatings        INT                     DEFAULT 0,
    TotalStudentErolled INT                     DEFAULT 0,
    CreatedAt           DATETIME                DEFAULT GETDATE(),
    UpdatedAt           DATETIME                DEFAULT GETDATE(),
    isChecked           BIT,
    FOREIGN KEY (UserID) REFERENCES Users (UserID),
    FOREIGN KEY (CategoryID) REFERENCES Categories (CategoryID)
);
GO

-- Bảng Lessons
CREATE TABLE Lessons
(
    LessonID    INT IDENTITY(1,1) PRIMARY KEY,
    CourseID    INT,
    Title       NVARCHAR(255) NOT NULL,
    Description NVARCHAR(500),
    ContentType NVARCHAR(20) CHECK (ContentType IN ('video', 'document', 'quiz', 'assignment')),
    Duration    INT      DEFAULT 0,
    IsFree      BIT      DEFAULT 0,
    CreatedAt   DATETIME DEFAULT GETDATE(),
    UpdatedAt   DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (CourseID) REFERENCES Courses (CourseID)
);
GO

-- Bảng Quizz
CREATE TABLE Quizz
(
    QuizzID       INT IDENTITY(1,1) PRIMARY KEY,
    LessonID      INT NOT NULL,
    Question      NVARCHAR(MAX) NOT NULL,
    CorrectAnswer NVARCHAR(255) NOT NULL,
    FOREIGN KEY (LessonID) REFERENCES Lessons (LessonID) ON DELETE CASCADE
);
GO

-- Bảng Video
CREATE TABLE Video
(
    VideoID    INT IDENTITY(1,1) PRIMARY KEY,
    LessonID   INT  NOT NULL,
    VideoURL   NVARCHAR(255) NOT NULL,
    Duration   TIME NOT NULL,
    UploadDate DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (LessonID) REFERENCES Lessons (LessonID) ON DELETE CASCADE
);
GO

CREATE TABLE Orders
(
    OrderID   INT IDENTITY(1,1) PRIMARY KEY,
    UserID    INT            NOT NULL,
    Amount    DECIMAL(10, 2) NOT NULL,
    OrderType NVARCHAR(20) CHECK (OrderType IN ('course_purchase', 'subscription')),
    Status    NVARCHAR(20) DEFAULT 'pending' CHECK (Status IN ('pending', 'completed')),
    OrderDate DATETIME DEFAULT GETDATE(),
    Note      NVARCHAR(500),
    FOREIGN KEY (UserID) REFERENCES Users (UserID)
);
GO

CREATE TABLE Order_Detail
(
    OrderID   INT NOT NULL,
    CourseID  INT NOT NULL,
    UnitPrice INT NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES Orders (OrderID),
    FOREIGN KEY (CourseID) REFERENCES Courses (CourseID)
);
GO

-- Bảng Enrollments
CREATE TABLE Enrollments
(
    EnrollmentID   INT IDENTITY(1,1) PRIMARY KEY,
    UserID         INT NOT NULL,
    CourseID       INT NOT NULL,
    EnrollmentDate DATETIME      DEFAULT GETDATE(),
    Progress       DECIMAL(5, 2) DEFAULT 0,
    Status         NVARCHAR(20) DEFAULT 'onGoing' CHECK (Status IN ('onGoing', 'completed')),
    PaymentID      INT,
    FOREIGN KEY (UserID) REFERENCES Users (UserID),
    FOREIGN KEY (CourseID) REFERENCES Courses (CourseID),
    FOREIGN KEY (PaymentID) REFERENCES Payments (PaymentID),
    CONSTRAINT UQ_User_Course UNIQUE (UserID, CourseID)
);
GO

-- Bảng CourseReviews
CREATE TABLE CourseReviews
(
    ReviewID     INT IDENTITY(1,1) PRIMARY KEY,
    EnrollmentID INT NOT NULL UNIQUE,
    CourseID     INT NOT NULL,
    Rating       INT NOT NULL CHECK (Rating BETWEEN 1 AND 5),
    Comment      NVARCHAR(1000),
    CreatedAt    DATETIME DEFAULT GETDATE(),
    UpdatedAt    DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (EnrollmentID) REFERENCES Enrollments (EnrollmentID),
    FOREIGN KEY (CourseID) REFERENCES Courses (CourseID)
);
GO

-- Bảng InstructorPackages
CREATE TABLE Packages
(
    PackageId      INT PRIMARY KEY IDENTITY(1,1),
    PackageName    NVARCHAR(100) NOT NULL,
    Price          DECIMAL(10, 2) NOT NULL,
    CoursesCreated INT            NOT NULL DEFAULT 0,
    Duration       INT            NOT NULL -- thời gian tính theo tháng
);

--mua gói
CREATE TABLE BuyPackages
(
    Id        INT PRIMARY KEY IDENTITY(1,1),
    UserId    INT            NOT NULL,
    PackageId INT            NOT NULL,
    Quantity  INT            NOT NULL CHECK (quantity > 0),
    Price     DECIMAL(10, 2) NOT NULL,
    Status    NVARCHAR(20) DEFAULT 'Active' CHECK (status IN ('Active', 'Expired', 'Pending')),
    ValidFrom DATE           NOT NULL,
    ValidTo   DATE           NOT NULL,
    CONSTRAINT FK_BuyPackages_Package FOREIGN KEY (PackageId) REFERENCES Packages (PackageId),
    CONSTRAINT FK_BuyPackages_User FOREIGN KEY (UserId) REFERENCES Users (UserId)
);
