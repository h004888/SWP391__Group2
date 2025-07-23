package com.OLearning.dto.chapter;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterProgress {
    private int totalLessons;
    private int completedLessons;
    private double percent;
    public ChapterProgress(int totalLessons, int completedLessons) {
        this.totalLessons = totalLessons;
        this.completedLessons = completedLessons;
        this.percent = totalLessons > 0 ? (100.0 * completedLessons / totalLessons) : 0.0;
    }

}
