package com.OLearning.mapper.instructorDashBoard;

import com.OLearning.dto.instructorDashboard.ChapterDTO;
import com.OLearning.entity.Chapters;
import org.springframework.stereotype.Component;

@Component
public class ChapterMapper {
    public Chapters dtoToChapter(ChapterDTO dto) {
        Chapters chapter = new Chapters();
        chapter.setTitle(dto.getTitle());
        chapter.setDescription(dto.getDescription());
        chapter.setOrderNumber(dto.getOrdernNumber());
        return chapter;
    }
}
