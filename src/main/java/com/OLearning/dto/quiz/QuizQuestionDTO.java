package com.OLearning.dto.quiz;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class QuizQuestionDTO {
    private Long id;
    @NotBlank(message = "Question is required")
    private String question;
    @NotBlank(message = "Option A is required")
    private String optionA;
    @NotBlank(message = "Option B is required")
    private String optionB;
    @NotBlank(message = "Option C is required")
    private String optionC;
    @NotBlank(message = "Option D is required")
    private String optionD;
    @NotBlank(message = "Correct answer is required")
    private String correctAnswer;
    @Min(value = 1, message = "Order number must be at least 1")
    @Digits(integer = 3, fraction = 0, message = "Order number must be numeric")
    private Integer orderNumber;
}
