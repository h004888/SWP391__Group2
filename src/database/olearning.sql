IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'Olearning')
BEGIN
    CREATE DATABASE Olearning;
END;
GO

USE Olearning;
GO

-- Bảng Roles
CREATE TABLE Roles (
                       RoleID SMALLINT IDENTITY(1,1) PRIMARY KEY,
                       Name NVARCHAR(20) NOT NULL UNIQUE
);
GO

-- Bảng Users
CREATE TABLE Users (
                       UserID INT IDENTITY(1,1) PRIMARY KEY,
                       Username NVARCHAR(50) NOT NULL UNIQUE,
                       RoleID SMALLINT,
                       Email NVARCHAR(100) NOT NULL UNIQUE,
                       Password NVARCHAR(255) NOT NULL, -- Mật khẩu đã hash
                       FullName NVARCHAR(100) NOT NULL,
                       Phone NVARCHAR(15),
                       Birthday DATETIME,
                       Address NVARCHAR(255),
                       ProfilePicture NVARCHAR(255),
                       FOREIGN KEY (RoleID) REFERENCES Roles(RoleID)
);
GO

-- Bảng Categories
CREATE TABLE Categories (
                            CategoryID INT IDENTITY(1,1) PRIMARY KEY,
                            Name NVARCHAR(100) NOT NULL
);
GO

-- Bảng Courses
CREATE TABLE Courses (
                         CourseID INT IDENTITY(1,1) PRIMARY KEY,
                         UserID INT NOT NULL,
                         CategoryID INT,
                         Title NVARCHAR(255) NOT NULL,
                         Description NVARCHAR(2000),
                         Price DECIMAL(10,2) NOT NULL DEFAULT 0,
                         Discount DECIMAL(5,2) DEFAULT 0,
                         CourseImg NVARCHAR(255),
                         Duration INT DEFAULT 0,
                         TotalLessons INT DEFAULT 0,
                         TotalRatings INT DEFAULT 0,
                         TotalStudentErolled INT DEFAULT 0,
                         CreatedAt DATETIME DEFAULT GETDATE(),
                         UpdatedAt DATETIME DEFAULT GETDATE(),
                         isChecked BIT, -- Sửa từ BOOLEAN thành BIT cho SQL Server
                         FOREIGN KEY (UserID) REFERENCES Users(UserID),
                         FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID)
);
GO

-- Bảng Lessons
CREATE TABLE Lessons (
                         LessonID INT IDENTITY(1,1) PRIMARY KEY,
                         CourseID INT,
                         Title NVARCHAR(255) NOT NULL,
                         Description NVARCHAR(500),
                         ContentType NVARCHAR(20) CHECK (ContentType IN ('video', 'document', 'quiz', 'assignment')),
                         Duration INT DEFAULT 0,
                         IsFree BIT DEFAULT 0,
                         CreatedAt DATETIME DEFAULT GETDATE(),
                         UpdatedAt DATETIME DEFAULT GETDATE(),
                         FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);
GO

-- Bảng Quizz
CREATE TABLE Quizz (
                       QuizzID INT IDENTITY(1,1) PRIMARY KEY, -- Sửa AUTO_INCREMENT thành IDENTITY
                       LessonID INT NOT NULL,
                       Question NVARCHAR(MAX) NOT NULL, -- Sửa TEXT thành NVARCHAR(MAX)
                       CorrectAnswer NVARCHAR(255) NOT NULL,
                       FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID) ON DELETE CASCADE
);
GO

-- Bảng Video
CREATE TABLE Video (
                       VideoID INT IDENTITY(1,1) PRIMARY KEY, -- Sửa AUTO_INCREMENT thành IDENTITY
                       LessonID INT NOT NULL,
                       VideoURL NVARCHAR(255) NOT NULL,
                       Duration TIME NOT NULL,
                       UploadDate DATETIME DEFAULT GETDATE(), -- Sửa CURRENT_TIMESTAMP thành GETDATE()
                       FOREIGN KEY (LessonID) REFERENCES Lessons(LessonID) ON DELETE CASCADE
);
GO

-- Bảng Payments
CREATE TABLE Payments (
                          PaymentID INT IDENTITY(1,1) PRIMARY KEY,
                          UserID INT NOT NULL,
                          CourseID INT NOT NULL,
                          Amount DECIMAL(10,2) NOT NULL,
                          PaymentType NVARCHAR(20) CHECK (PaymentType IN ('course_purchase', 'subscription')),
                          Status NVARCHAR(20) DEFAULT 'pending' CHECK (Status IN ('pending', 'completed')),
                          PaymentDate DATETIME DEFAULT GETDATE(),
                          Note NVARCHAR(500),
                          FOREIGN KEY (UserID) REFERENCES Users(UserID),
                          FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);
GO
drop table Payments
-- Bảng Enrollments
CREATE TABLE Enrollments (
                             EnrollmentID INT IDENTITY(1,1) PRIMARY KEY,
                             UserID INT NOT NULL,
                             CourseID INT NOT NULL,
                             EnrollmentDate DATETIME DEFAULT GETDATE(),
                             Progress DECIMAL(5,2) DEFAULT 0,
                             Status NVARCHAR(20) DEFAULT 'onGoing' CHECK (Status IN ('onGoing', 'completed')),
                             PaymentID INT,
                             FOREIGN KEY (UserID) REFERENCES Users(UserID),
                             FOREIGN KEY (CourseID) REFERENCES Courses(CourseID),
                             FOREIGN KEY (PaymentID) REFERENCES Payments(PaymentID),
                             CONSTRAINT UQ_User_Course UNIQUE (UserID, CourseID)
);
GO

-- Bảng CourseReviews
CREATE TABLE CourseReviews (
                               ReviewID INT IDENTITY(1,1) PRIMARY KEY,
                               EnrollmentID INT NOT NULL UNIQUE,
                               CourseID INT NOT NULL,
                               Rating INT NOT NULL CHECK (Rating BETWEEN 1 AND 5),
                               Comment NVARCHAR(1000),
                               CreatedAt DATETIME DEFAULT GETDATE(),
                               UpdatedAt DATETIME DEFAULT GETDATE(),
                               FOREIGN KEY (EnrollmentID) REFERENCES Enrollments(EnrollmentID),
                               FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);
GO

-- Bảng Cart_Items
CREATE TABLE Cart_Items (
                            CartItemID BIGINT IDENTITY(1,1) PRIMARY KEY,
                            UserID INT NOT NULL,
                            CourseID INT,
                            AddedAt DATETIME DEFAULT GETDATE(),
                            Quantity INT DEFAULT 1,
                            FOREIGN KEY (UserID) REFERENCES Users(UserID),
                            FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);
GO