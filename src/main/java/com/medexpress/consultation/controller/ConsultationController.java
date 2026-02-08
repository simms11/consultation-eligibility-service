package com.medexpress.consultation.controller;

import com.medexpress.consultation.model.SubmitConsultationRequest;
import com.medexpress.consultation.model.EligibilityResponse;
import com.medexpress.consultation.model.Question;
import com.medexpress.consultation.service.ConsultationService;

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

    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getQuestions(){

        return ResponseEntity.ok(consultationService.getAllQuestions());
    }

    @PostMapping("/submit")
    public ResponseEntity<EligibilityResponse> submitConsultation(@RequestBody SubmitConsultationRequest submission){
        EligibilityResponse decision = consultationService.processSubmission(submission);

        return ResponseEntity.ok(decision);
    }

}
