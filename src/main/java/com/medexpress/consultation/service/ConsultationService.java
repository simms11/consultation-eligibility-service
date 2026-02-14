package com.medexpress.consultation.service;

import com.medexpress.consultation.exception.ConsultationValidationException;
import com.medexpress.consultation.model.*;
import com.medexpress.consultation.repository.ProductRepository;
import com.medexpress.consultation.repository.QuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ConsultationService {

    private final QuestionRepository questionRepository;
    private final ProductRepository productRepository;

    public ConsultationService(QuestionRepository questionRepository, ProductRepository productRepository) {
        this.questionRepository = questionRepository;
        this.productRepository = productRepository;
    }

    public Map<String, String> validateAndMapAnswers(SubmitConsultationRequest request) {
        log.debug("Validating consultation request for product: {}", request.productId());

        if (request.productId() == null) {
            throw new ConsultationValidationException("Product ID is required.");
        }

        String productIdStr = request.productId().getValue();

        productRepository.findById(productIdStr)
                .orElseThrow(() -> new ConsultationValidationException("Product not found: " + productIdStr));

        List<Question> productQuestions = questionRepository.findByProductId(productIdStr);

        if (productQuestions.isEmpty()) {
            throw new ConsultationValidationException("No questions configured for Product ID: " + productIdStr);
        }

        int expectedQuestionCount = productQuestions.size();

        if (request.answers().size() != expectedQuestionCount) {
            throw new ConsultationValidationException("Expected: " + expectedQuestionCount + " answers");
        }

        Set<String> validQuestionIDs = productQuestions.stream()
                .map(Question::id)
                .collect(Collectors.toSet());

        Map<String, String> validatedAnswers = new HashMap<>();

        for (ConsultationAnswer answer : request.answers()) {
            if (validatedAnswers.containsKey(answer.questionId())) {
                throw new ConsultationValidationException("Duplicate answer for question: " + answer.questionId());
            }

            if (!validQuestionIDs.contains(answer.questionId())) {
                throw new ConsultationValidationException("Invalid question ID: " + answer.questionId());
            }

            Question question = questionRepository.findById(answer.questionId()).orElseThrow();

            if (!question.options().contains(answer.answer())) {
                throw new ConsultationValidationException("Invalid answer '" + answer.answer() + "' for question " + answer.questionId() +
                        ". Valid options: " + question.options());
            }
            validatedAnswers.put(answer.questionId(), answer.answer());
        }
        return validatedAnswers;
    }

    public EligibilityResponse checkEligibility(Map<String, String> userAnswers, List<Question> rules) {
        for (Question rule : rules) {
            String userAnswer = userAnswers.get(rule.id());

            if (rule.specificOutcomes().containsKey(userAnswer)) {
                String specificMessage = rule.specificOutcomes().get(userAnswer);
                log.info("Specific outcome triggered for question {}: {}", rule.id(), specificMessage);
                return new EligibilityResponse(false, specificMessage);
            }

            if (!rule.expectedAnswer().equals(userAnswer)) {
                log.info("Answer '{}' does not match expected '{}' for question {}",
                        userAnswer, rule.expectedAnswer(), rule.id());
                return new EligibilityResponse(false, rule.rejectedMessage());
            }
        }
        return new EligibilityResponse(true, null);
    }

    public EligibilityResponse processSubmission(SubmitConsultationRequest submission) {
        log.info("Processing consultation submission for product: {}", submission.productId());

        try {
            Map<String, String> validatedAnswers = validateAndMapAnswers(submission);
            log.debug("Validated {} answers for product {}", validatedAnswers.size(), submission.productId());

            String productIdStr = submission.productId().getValue();
            List<Question> productRules = questionRepository.findByProductId(productIdStr);

            EligibilityResponse response = checkEligibility(validatedAnswers, productRules);

            log.info("Eligibility decision for product {}: eligible={}, message={}",
                    submission.productId(), response.eligible(), response.message());

            return response;
        } catch (ConsultationValidationException e) {
            log.warn("Validation failed for product {}: {}", submission.productId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error processing consultation for product {}", submission.productId(), e);
            throw e;
        }
    }

    public List<QuestionDTO> getQuestionsByProduct(String productID) {
        return questionRepository.findByProductId(productID)
                .stream()
                .map(QuestionDTO::fromEntity)
                .collect(Collectors.toList());
    }
}