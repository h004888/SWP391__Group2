package com.OLearning.dto.instructorRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstructorRequestDTO {
    private Long requestId;

    private LocalDateTime requestDate;

    private String status;

    private LocalDateTime decisionDate;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Personal Skill is required")
    private String personalSkill;

    private String note;
} 