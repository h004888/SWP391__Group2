package com.OLearning.dto.quiz;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizSubmissionForm {
    private Long quizId;
    private Long courseId;
    private Long lessonId;
    private Map<Integer, String> answers = new HashMap<>();
}
    