package com.medexpress.consultation.service;

import com.medexpress.consultation.model.ConsultationAnswer;
import com.medexpress.consultation.model.EligibilityResponse;
import com.medexpress.consultation.model.Question;
import com.medexpress.consultation.model.SubmitConsultationRequest;
import com.medexpress.consultation.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ConsultationService {
    private final QuestionRepository questionRepository;

    public ConsultationService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }


    public Map<String, String> validateAndMapAnswers(SubmitConsultationRequest submitConsultationRequest){
        int expectedQuestionCount = questionRepository.findAll().size();

        if(submitConsultationRequest.answers().size()!=expectedQuestionCount)
            throw new IllegalArgumentException("Expected: "+ expectedQuestionCount + " answers");

        Map<String, String> validatedAnswers = new HashMap<>();


        for(ConsultationAnswer answer: submitConsultationRequest.answers()){

            if(validatedAnswers.containsKey(answer.questionId())){
                throw new IllegalArgumentException("Duplicate answer for question: " + answer.questionId());
            }

            Optional<Question> questionOpt = questionRepository.findById(answer.questionId());
            if(questionOpt.isEmpty()){
                throw new IllegalArgumentException("Invalid question ID: " + answer.questionId());
            }

            Question question = questionOpt.get();
            if(!question.options().contains(answer.answer())){
                throw new IllegalArgumentException("Invalid answer '" + answer.answer() + "' for question " + answer.questionId() +
                        ". Valid options: " + question.options());
            }
            validatedAnswers.put(answer.questionId(), answer.answer());
        }


        return validatedAnswers;
    }


    public EligibilityResponse checkEligibility(Map<String,String> userAnswers) {


        for (Question rule : questionRepository.findAll().values()){
            String userAnswer = userAnswers.get(rule.id());

            if (!rule.expectedAnswer().equals(userAnswer)){
                return new EligibilityResponse(false, rule.rejectedMessage());
            }
        }


        return new EligibilityResponse(true, null);
    }

    public List<Question> getAllQuestions(){
        return new ArrayList<>(questionRepository.findAll().values());
    }

    public EligibilityResponse processSubmission(SubmitConsultationRequest submission){

        Map<String, String> validatedAnswers = validateAndMapAnswers(submission);

       return checkEligibility(validatedAnswers);
    }
}
