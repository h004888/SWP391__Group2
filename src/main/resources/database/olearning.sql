ALTER TABLE CourseReviews ADD COLUMN parent_review_id BIGINT;
ALTER TABLE CourseReviews ADD CONSTRAINT fk_parent_review FOREIGN KEY (parent_review_id) REFERENCES CourseReviews(reviewId); 