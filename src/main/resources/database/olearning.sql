ALTER TABLE CourseReviews ADD COLUMN parent_review_id BIGINT;
ALTER TABLE CourseReviews ADD CONSTRAINT fk_parent_review FOREIGN KEY (parent_review_id) REFERENCES CourseReviews(reviewId); 
ALTER TABLE CourseReviews ADD COLUMN Hidden BOOLEAN NOT NULL DEFAULT 0; 
ALTER TABLE CourseReviews ADD COLUMN CommentID BIGINT; 