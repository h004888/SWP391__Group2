package com.OLearning.dto.quiz;


import com.OLearning.dto.video.VideoDTO;
import com.OLearning.entity.Lesson;
import com.OLearning.entity.Video;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class QuizDTO{
    private Long id;
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Description is required")
    private String description;
    @Min(value = 1, message = "Time limit must be at least 1 minute")
    @Digits(integer = 3, fraction = 0, message = "Time limit must be numeric")
    private Integer timeLimit;
    private Long lessonId;
    private List<QuizQuestionDTO> questions = new ArrayList<>();
}