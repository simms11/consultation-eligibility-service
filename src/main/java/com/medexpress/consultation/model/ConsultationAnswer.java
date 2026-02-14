package com.medexpress.consultation.model;

import jakarta.validation.constraints.NotBlank;

public record ConsultationAnswer(
        @NotBlank(message = "Question ID is required")
        String questionId,

        @NotBlank(message = "Answer cannot be blank")
        String answer
) {}