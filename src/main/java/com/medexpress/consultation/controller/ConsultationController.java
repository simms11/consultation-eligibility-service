package com.medexpress.consultation.controller;

import com.medexpress.consultation.model.*;
import com.medexpress.consultation.service.ConsultationService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultation")
public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @GetMapping("/products/{productId}/questions")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByProduct(@PathVariable ProductId productId) {

        List<QuestionDTO> questions = consultationService.getQuestionsByProduct(productId.getValue());

        if (questions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/submit")
    public ResponseEntity<EligibilityResponse> submitConsultation(@Valid @RequestBody SubmitConsultationRequest submission) {
        EligibilityResponse decision = consultationService.processSubmission(submission);

        return ResponseEntity.ok(decision);
    }

}
