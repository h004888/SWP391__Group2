package com.OLearning.service.instructorDashboard;

import com.OLearning.dto.instructorDashboard.ChapterDTO;
import com.OLearning.entity.Chapters;

import java.util.List;

public interface ChapterService {
    Chapters saveChapter(ChapterDTO chapterDTO);
}
