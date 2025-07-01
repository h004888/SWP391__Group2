package com.OLearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "Quiz_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QuestionID")
    private Long id;
    
    @Column(name = "Question")
    private String question;
    
    @Column(name = "OptionA")
    private String optionA;
    
    @Column(name = "OptionB")
    private String optionB;
    
    @Column(name = "OptionC")
    private String optionC;
    
    @Column(name = "OptionD")
    private String optionD;
    
    @Column(name = "CorrectAnswer")
    private String correctAnswer;
    
    @Column(name = "OrderNumber")
    private Integer orderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QuizID")
    private Quiz quiz;
}
