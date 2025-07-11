-- Script để update database hiện tại cho phép rating null
-- Chạy script này nếu database đã tồn tại

-- 1. Xóa constraint cũ nếu có
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.CHECK_CONSTRAINTS WHERE CONSTRAINT_NAME = 'CK__CourseRev__Rating__XXXXX')
BEGIN
    ALTER TABLE CourseReviews DROP CONSTRAINT CK__CourseRev__Rating__XXXXX;
END

-- 2. Thay đổi cột Rating để cho phép NULL
ALTER TABLE CourseReviews ALTER COLUMN Rating INT NULL;

-- 3. Thêm constraint mới cho phép NULL hoặc giá trị từ 1-5
ALTER TABLE CourseReviews ADD CONSTRAINT CK_CourseReviews_Rating 
    CHECK (Rating IS NULL OR (Rating BETWEEN 1 AND 5));

-- 4. Thêm cột Status vào bảng Courses nếu chưa có
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Courses' AND COLUMN_NAME = 'Status')
BEGIN
    ALTER TABLE Courses ADD Status NVARCHAR(20) DEFAULT 'pending';
END

-- 5. Thêm cột CanResubmit vào bảng Courses nếu chưa có
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Courses' AND COLUMN_NAME = 'CanResubmit')
BEGIN
    ALTER TABLE Courses ADD CanResubmit BIT DEFAULT 0;
END

-- 6. Thêm cột CourseLevel vào bảng Courses nếu chưa có
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Courses' AND COLUMN_NAME = 'CourseLevel')
BEGIN
    ALTER TABLE Courses ADD CourseLevel NVARCHAR(20) DEFAULT 'Beginner';
END

-- 7. Thêm cột hidden vào bảng CourseReviews nếu chưa có
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'CourseReviews' AND COLUMN_NAME = 'hidden')
BEGIN
    ALTER TABLE CourseReviews ADD hidden BIT NOT NULL DEFAULT 0;
END

-- 8. Thêm cột parentId vào bảng CourseReviews nếu chưa có
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'CourseReviews' AND COLUMN_NAME = 'parentId')
BEGIN
    ALTER TABLE CourseReviews ADD parentId INT NULL;
    ALTER TABLE CourseReviews ADD CONSTRAINT FK_CourseReviews_Parent FOREIGN KEY (parentId) REFERENCES CourseReviews(ReviewID);
END

-- Add lessonId column to CourseReviews table for lesson-specific comments
ALTER TABLE CourseReviews ADD COLUMN lessonId BIGINT NULL;
ALTER TABLE CourseReviews ADD CONSTRAINT fk_course_review_lesson FOREIGN KEY (lessonId) REFERENCES Lessons(LessonID);

-- Update existing comments to have lessonId = NULL (course-level comments)
-- This maintains backward compatibility

-- Allow Rating column to be NULL for comments
ALTER TABLE CourseReviews ALTER COLUMN Rating INT NULL;

PRINT 'Database updated successfully!'; 