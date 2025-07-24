package com.OLearning.dto.enrollment;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentFilterDTO {
    private String searchTerm;
    private Long courseId;
    private String status;
    private int page = 0;
    private int size = 5;
} 