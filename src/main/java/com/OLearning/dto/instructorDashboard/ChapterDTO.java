package com.OLearning.dto.instructorDashboard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChapterDTO {
    private String title;
    private String description;
    private Integer ordernNumber;
    private Long courseId;

}
