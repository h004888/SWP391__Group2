package com.OLearning.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAIResponseDTO {
    private List<Candidate> candidates;
    private PromptFeedback promptFeedback;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Candidate {
        private Content content;
        private String finishReason;
        private int index;
        private List<SafetyRating> safetyRatings;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;
        private String role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String text;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SafetyRating {
        private String category;
        private String probability;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PromptFeedback {
        private List<SafetyRating> safetyRatings;
    }
} 