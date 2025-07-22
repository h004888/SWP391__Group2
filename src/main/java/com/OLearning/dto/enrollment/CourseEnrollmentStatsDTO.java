package com.OLearning.dto.enrollment;

public class CourseEnrollmentStatsDTO {
    private Long courseId;
    private String courseTitle;
    private long totalEnroll;
    private long completedEnroll;

    public Long getCourseId() {
        return courseId;
    }
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    public String getCourseTitle() {
        return courseTitle;
    }
    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
    public long getTotalEnroll() {
        return totalEnroll;
    }
    public void setTotalEnroll(long totalEnroll) {
        this.totalEnroll = totalEnroll;
    }
    public long getCompletedEnroll() {
        return completedEnroll;
    }
    public void setCompletedEnroll(long completedEnroll) {
        this.completedEnroll = completedEnroll;
    }
} 