package com.OLearning.dto.course;

public class CourseSalesDTO {
    private String courseName;
    private int sold;
    private double revenue;

    public CourseSalesDTO(String courseName, int sold, double revenue) {
        this.courseName = courseName;
        this.sold = sold;
        this.revenue = revenue;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
} 