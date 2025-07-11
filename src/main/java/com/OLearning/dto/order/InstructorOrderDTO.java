package com.OLearning.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstructorOrderDTO {
    private Long orderId;
    private double instructorAmount; // Amount only for instructor's courses
    private String orderType;
    private String status;
    private LocalDateTime orderDate;
    private String username;
    private int instructorCourseCount; // Number of instructor's courses in this order
} 