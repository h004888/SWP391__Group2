package com.OLearning.service.chapter;

import com.OLearning.dto.chapter.ChapterDTO;
import com.OLearning.entity.Chapter;

import java.util.List;

public interface ChapterService {
    Chapter saveChapter(ChapterDTO chapterDTO);
    List<Chapter> chapterListByCourse(Long courserId);
    Chapter getChapterById(Long id);
    void updateChapter(Chapter chapter);
}
