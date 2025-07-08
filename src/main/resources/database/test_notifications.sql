-- Test notifications for user (assuming user ID = 1)
-- Insert test notifications for user

-- Enrollment notification
INSERT INTO notifications (user_id, type, message, status, sent_at, course_id)
VALUES (1, 'ENROLLMENT', 'You have successfully enrolled in the course "Java Programming Fundamentals"', 'failed', GETDATE(), 1);

-- Course completion notification
INSERT INTO notifications (user_id, type, message, status, sent_at, course_id)
VALUES (1, 'COURSE_COMPLETION', 'Congratulations! You have completed the course "Java Programming Fundamentals"', 'failed', GETDATE(), 1);

-- Quiz result notification
INSERT INTO notifications (user_id, type, message, status, sent_at, course_id)
VALUES (1, 'QUIZ_RESULT', 'You scored 85% on the "Java Basics Quiz"', 'failed', GETDATE(), 1);

-- Certificate notification
INSERT INTO notifications (user_id, type, message, status, sent_at, course_id)
VALUES (1, 'CERTIFICATE', 'Your certificate for "Java Programming Fundamentals" is ready for download', 'failed', GETDATE(), 1);

-- Payment success notification
INSERT INTO notifications (user_id, type, message, status, sent_at, course_id)
VALUES (1, 'PAYMENT_SUCCESS', 'Payment of $29.99 for "Advanced Java Course" was successful', 'failed', GETDATE(), 2);

-- Payment failed notification
INSERT INTO notifications (user_id, type, message, status, sent_at, course_id)
VALUES (1, 'PAYMENT_FAILED', 'Payment of $19.99 for "Python Basics" failed. Please try again', 'failed', GETDATE(), 3);

-- Add more notifications for testing pagination
INSERT INTO notifications (user_id, type, message, status, sent_at, course_id)
VALUES (1, 'ENROLLMENT', 'You have successfully enrolled in the course "Web Development with React"', 'failed', DATEADD(minute, -30, GETDATE()), 4);

INSERT INTO notifications (user_id, type, message, status, sent_at, course_id)
VALUES (1, 'QUIZ_RESULT', 'You scored 92% on the "React Fundamentals Quiz"', 'failed', DATEADD(hour, -2, GETDATE()), 4);

INSERT INTO notifications (user_id, type, message, status, sent_at, course_id)
VALUES (1, 'COURSE_COMPLETION', 'Congratulations! You have completed the course "Web Development with React"', 'failed', DATEADD(hour, -5, GETDATE()), 4);

-- Add some read notifications
INSERT INTO notifications (user_id, type, message, status, sent_at, course_id)
VALUES (1, 'ENROLLMENT', 'You have successfully enrolled in the course "Database Design"', 'sent', DATEADD(day, -1, GETDATE()), 5);

INSERT INTO notifications (user_id, type, message, status, sent_at, course_id)
VALUES (1, 'QUIZ_RESULT', 'You scored 78% on the "SQL Basics Quiz"', 'sent', DATEADD(day, -2, GETDATE()), 5); 