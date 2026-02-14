package com.medexpress.consultation.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SubmitConsultationRequest(
        @NotNull(message = "Product ID is required")
        ProductId productId,

        @NotNull(message = "Answers list cannot be null")
        @NotEmpty(message = "You must answer at least one question")
        @Valid
        List<ConsultationAnswer> answers
) {}