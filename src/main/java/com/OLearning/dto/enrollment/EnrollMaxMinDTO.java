package com.OLearning.dto.enrollment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EnrollMaxMinDTO {
    private Long enrollmentCount;
    private Long courseId;
    private String courseImg;
    private BigDecimal averageProgress;
    private String courseLevel;
    private Timestamp enrollmentDate;
    private String title;
}
