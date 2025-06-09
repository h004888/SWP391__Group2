package com.OLearning.mapper.chapter;

import com.OLearning.dto.chapter.ChapterDTO;
import com.OLearning.entity.Chapter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ChapterMapper {
    public Chapter dtoToChapter(ChapterDTO dto) {
        Chapter chapter = new Chapter();
        chapter.setTitle(dto.getTitle());
        chapter.setDescription(dto.getDescription());
        chapter.setOrderNumber(dto.getOrdernNumber());
        chapter.setCreateAt(LocalDateTime.now());
        chapter.setUpdateAt(LocalDateTime.now());
        return chapter;
    }
}
