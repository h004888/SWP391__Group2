package com.OLearning.service.instructorDashBoard.FileHelper;

import java.util.UUID;
//rename image, avoid like image path
public class FileHelper {
        public static String generateFileName(String fileName) {
            int lastIndex = fileName.lastIndexOf(".");
            String ext = fileName.substring(lastIndex);
            return UUID.randomUUID().toString().replace("-", "") + ext;
        }
    }
