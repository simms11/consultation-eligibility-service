package com.medexpress.consultation.model;

import java.util.List;

public record QuestionDTO(
        String id,
        String productId,
        String text,
        List<String> options
) {
    public static QuestionDTO fromEntity(Question question) {
        return new QuestionDTO(
                question.id(),
                question.productId(),
                question.text(),
                question.options()
        );
    }
}