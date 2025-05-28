package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "Courses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    private String title;
    private String description;
    private Double price;
    private Double discount;
    private String courseImg;
    private Integer duration;
    private Integer totalLessons;
    private Integer totalRatings;
    private Integer totalStudentEnrolled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isChecked;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User instructor;

    @ManyToOne
    @JoinColumn(name = "CategoryID")
    private Categories category;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Lessons> listOfLessons;

    public List<Lessons> getListOfLessons() {
        return listOfLessons;
    }

    public void setListOfLessons(List<Lessons> listOfLessons) {
        this.listOfLessons = listOfLessons;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getCourseImg() {
        return courseImg;
    }

    public void setCourseImg(String courseImg) {
        this.courseImg = courseImg;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(Integer totalLessons) {
        this.totalLessons = totalLessons;
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }

    public Integer getTotalStudentEnrolled() {
        return totalStudentEnrolled;
    }

    public void setTotalStudentEnrolled(Integer totalStudentEnrolled) {
        this.totalStudentEnrolled = totalStudentEnrolled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }
}

