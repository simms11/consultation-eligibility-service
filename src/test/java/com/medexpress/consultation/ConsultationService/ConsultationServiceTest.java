package com.medexpress.consultation.ConsultationService;

import com.medexpress.consultation.exception.ConsultationValidationException;
import com.medexpress.consultation.model.*;
import com.medexpress.consultation.repository.ProductRepository;
import com.medexpress.consultation.repository.QuestionRepository;
import com.medexpress.consultation.service.ConsultationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.medexpress.consultation.constant.ConsultationConstants.Products.*;
import static com.medexpress.consultation.constant.ConsultationConstants.Questions.*;
import static com.medexpress.consultation.constant.ConsultationConstants.Answers.*;
import static com.medexpress.consultation.constant.ConsultationConstants.Messages.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ProductRepository productRepository;

    private ConsultationService consultationService;

    @BeforeEach
    void setUp() {
        consultationService = new ConsultationService(questionRepository, productRepository);
    }

    private void mockStandardQuestions() {

        Question q1 = new Question(ALLERGY_PEAR, GENOVIAN_PEAR, "Allergy?", List.of(YES, NO), NO, ALLERGY_REJECT, null);
        Question q2 = new Question(AGE_OVER_18, GENOVIAN_PEAR, "Age?", List.of(YES, NO), YES, AGE_REJECT, null);
        Question q3 = new Question(PREGNANCY, GENOVIAN_PEAR, "Pregnant?", List.of(YES, NO), NO, PREGNANCY_REJECT, null);

        Map<String, String> bpOutcomes = Map.of(LOW, BP_LOW_CONTACT);

        Question q4 = new Question(BLOOD_PRESSURE, GENOVIAN_PEAR, "BP?",
                List.of(LOW, NORMAL, HIGH),
                NORMAL,
                BP_HIGH_REJECT,
                bpOutcomes);

        List<Question> allQuestions = List.of(q1, q2, q3, q4);

        Product mockProduct = new Product(GENOVIAN_PEAR, "Genovian Pear", true);
        lenient().when(productRepository.findById(GENOVIAN_PEAR)).thenReturn(Optional.of(mockProduct));

        lenient().when(questionRepository.findByProductId(GENOVIAN_PEAR)).thenReturn(allQuestions);

        lenient().when(questionRepository.findById(ALLERGY_PEAR)).thenReturn(Optional.of(q1));
        lenient().when(questionRepository.findById(AGE_OVER_18)).thenReturn(Optional.of(q2));
        lenient().when(questionRepository.findById(PREGNANCY)).thenReturn(Optional.of(q3));
        lenient().when(questionRepository.findById(BLOOD_PRESSURE)).thenReturn(Optional.of(q4));
    }

    @Test
    void shouldReturnEligible_WhenAllAnswersAreCorrect() {
        mockStandardQuestions();

        List<ConsultationAnswer> answers = List.of(
                new ConsultationAnswer(ALLERGY_PEAR, NO),
                new ConsultationAnswer(AGE_OVER_18, YES),
                new ConsultationAnswer(PREGNANCY, NO),
                new ConsultationAnswer(BLOOD_PRESSURE, NORMAL)
        );

        SubmitConsultationRequest request = new SubmitConsultationRequest(ProductId.GENOVIAN_PEAR, answers);

        EligibilityResponse result = consultationService.processSubmission(request);

        assertTrue(result.eligible());
    }

    @Test
    void shouldReturnIneligible_WhenUserIsAllergicToPear() {
        mockStandardQuestions();

        List<ConsultationAnswer> answers = List.of(
                new ConsultationAnswer(ALLERGY_PEAR, YES),
                new ConsultationAnswer(AGE_OVER_18, YES),
                new ConsultationAnswer(PREGNANCY, NO),
                new ConsultationAnswer(BLOOD_PRESSURE, NORMAL)
        );

        SubmitConsultationRequest request = new SubmitConsultationRequest(ProductId.GENOVIAN_PEAR, answers);

        EligibilityResponse result = consultationService.processSubmission(request);

        assertFalse(result.eligible());
        assertEquals(ALLERGY_REJECT, result.message());
    }

    @Test
    void shouldReturnContactSupport_WhenBloodPressureIsLow() {
        mockStandardQuestions();

        List<ConsultationAnswer> answers = List.of(
                new ConsultationAnswer(ALLERGY_PEAR, NO),
                new ConsultationAnswer(AGE_OVER_18, YES),
                new ConsultationAnswer(PREGNANCY, NO),
                new ConsultationAnswer(BLOOD_PRESSURE, LOW)
        );

        SubmitConsultationRequest request = new SubmitConsultationRequest(ProductId.GENOVIAN_PEAR, answers);

        EligibilityResponse result = consultationService.processSubmission(request);

        assertFalse(result.eligible());
        assertEquals(BP_LOW_CONTACT, result.message());
    }

    @Test
    void shouldReturnIneligible_WhenBloodPressureIsHigh() {
        mockStandardQuestions();

        List<ConsultationAnswer> answers = List.of(
                new ConsultationAnswer(ALLERGY_PEAR, NO),
                new ConsultationAnswer(AGE_OVER_18, YES),
                new ConsultationAnswer(PREGNANCY, NO),
                new ConsultationAnswer(BLOOD_PRESSURE, HIGH)
        );

        SubmitConsultationRequest request = new SubmitConsultationRequest(ProductId.GENOVIAN_PEAR, answers);

        EligibilityResponse result = consultationService.processSubmission(request);

        assertFalse(result.eligible());
        assertEquals(BP_HIGH_REJECT, result.message());
    }

    @Test
    void shouldThrowException_WhenProductIdIsNull() {
        List<ConsultationAnswer> answers = List.of(
                new ConsultationAnswer(ALLERGY_PEAR, YES)
        );

        SubmitConsultationRequest request = new SubmitConsultationRequest(null, answers);

        assertThrows(ConsultationValidationException.class, () -> {
            consultationService.processSubmission(request);
        });
    }

    @Test
    void shouldThrowException_WhenMissingAnswers() {
        mockStandardQuestions();

        List<ConsultationAnswer> answers = List.of(
                new ConsultationAnswer(ALLERGY_PEAR, NO)
        );

        SubmitConsultationRequest request = new SubmitConsultationRequest(ProductId.GENOVIAN_PEAR, answers);

        assertThrows(ConsultationValidationException.class, () -> {
            consultationService.processSubmission(request);
        });
    }
}
