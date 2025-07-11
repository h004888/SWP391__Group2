-- Migration script to add IsGooglePicture column to Users table
-- This script should be run to update existing database

-- Add IsGooglePicture column to Users table
ALTER TABLE Users ADD IsGooglePicture BIT DEFAULT 0;

-- Update existing users to set IsGooglePicture = 1 for users who have profile pictures
-- (assuming existing profile pictures are from Google OAuth2)
UPDATE Users SET IsGooglePicture = 1 WHERE ProfilePicture IS NOT NULL AND ProfilePicture != '';

-- Set IsGooglePicture = 0 for users without profile pictures
UPDATE Users SET IsGooglePicture = 0 WHERE ProfilePicture IS NULL OR ProfilePicture = ''; 