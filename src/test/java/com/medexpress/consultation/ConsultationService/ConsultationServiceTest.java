package com.medexpress.consultation.ConsultationService;

import com.medexpress.consultation.model.ConsultationAnswer;
import com.medexpress.consultation.model.SubmitConsultationRequest;
import com.medexpress.consultation.model.EligibilityResponse;
import com.medexpress.consultation.model.Question;
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

import static com.medexpress.consultation.constant.ConsultationConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConsultationServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    private ConsultationService consultationService;

    @BeforeEach
    void setUp(){
        consultationService = new ConsultationService(questionRepository);

    }

    private void mockQuestion() {
        Map<String, Question> questionMap = Map.of(
                QUESTION_ALLERGY_PEAR, new Question(QUESTION_ALLERGY_PEAR, "Text", List.of(YES, NO), YES, ALLERGY_REJECT ),
                QUESTION_AGE_OVER_18, new Question(QUESTION_AGE_OVER_18, "Text", List.of(YES, NO), YES, AGE_REJECT),
                QUESTION_PREGNANCY, new Question(QUESTION_PREGNANCY, "Text", List.of(YES, NO), NO,PREGNANCY_REJECT)
        );

        when(questionRepository.findAll()).thenReturn(questionMap);

        when(questionRepository.findById(QUESTION_ALLERGY_PEAR)).thenReturn(Optional.of(questionMap.get(QUESTION_ALLERGY_PEAR)));
        when(questionRepository.findById(QUESTION_AGE_OVER_18)).thenReturn(Optional.of(questionMap.get(QUESTION_AGE_OVER_18)));
        when(questionRepository.findById(QUESTION_PREGNANCY)).thenReturn(Optional.of(questionMap.get(QUESTION_PREGNANCY)));

    }

    @Test
    void shouldReturnEligible_WhenAllAnswersAreCorrect() {
        mockQuestion();

        List<ConsultationAnswer> answers = List.of(
                new ConsultationAnswer(QUESTION_ALLERGY_PEAR, YES),
                new ConsultationAnswer(QUESTION_AGE_OVER_18, YES),
                new ConsultationAnswer(QUESTION_PREGNANCY, NO)
        );

        EligibilityResponse result = consultationService.processSubmission(new SubmitConsultationRequest(answers));

        assertTrue(result.eligible(), "User should be eligible");
    }

    @Test
    void shouldReturnIneligible_WhenUserIsNotAllergicToPear(){
        mockQuestion();

        List<ConsultationAnswer> answers = List.of(
                new ConsultationAnswer(QUESTION_ALLERGY_PEAR, NO),
                new ConsultationAnswer(QUESTION_AGE_OVER_18, YES),
                new ConsultationAnswer(QUESTION_PREGNANCY, NO)
        );

        EligibilityResponse result = consultationService.processSubmission(new SubmitConsultationRequest(answers));
        assertFalse(result.eligible());
        assertEquals(ALLERGY_REJECT, result.message());

    }

    @Test
    void shouldReturnIneligible_WhenUserIsPregnant(){
        mockQuestion();

        List<ConsultationAnswer> answers = List.of(
                new ConsultationAnswer(QUESTION_ALLERGY_PEAR, YES),
                new ConsultationAnswer(QUESTION_AGE_OVER_18, YES),
                new ConsultationAnswer(QUESTION_PREGNANCY, YES)
        );

        EligibilityResponse result = consultationService.processSubmission(new SubmitConsultationRequest(answers));
        assertFalse(result.eligible());
        assertEquals(PREGNANCY_REJECT, result.message());

    }
}
