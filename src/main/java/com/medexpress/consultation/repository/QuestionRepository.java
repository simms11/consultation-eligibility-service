package com.medexpress.consultation.repository;

import com.medexpress.consultation.model.Question;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.medexpress.consultation.constant.ConsultationConstants.*;

@Repository
public class QuestionRepository {


    private static final Map<String, Question> QUESTIONS = Map.of(
            QUESTION_ALLERGY_PEAR, new Question(QUESTION_ALLERGY_PEAR,
                    "Have you been diagnosed with an allergy to Genovian Pears?",
                    List.of(YES, NO),
                    YES,
                    ALLERGY_REJECT),

            QUESTION_AGE_OVER_18, new Question(QUESTION_AGE_OVER_18,
                    "Are you over 18 years of age?",
                    List.of(YES, NO),
                    YES,
                    AGE_REJECT),

            QUESTION_PREGNANCY, new Question(QUESTION_PREGNANCY,
                    "Are you currently pregnant or breastfeeding?",
                    List.of(YES, NO),
                    NO,PREGNANCY_REJECT)
    );

    public Map<String, Question> findAll(){
        return QUESTIONS;
    }

    public Optional<Question> findById(String id) {
        return Optional.ofNullable(QUESTIONS.get(id));
    }
}
