package com.medexpress.consultation.constant;

public class ConsultationConstants {

    private ConsultationConstants() {}

    //QuestionId
    public static final String QUESTION_ALLERGY_PEAR = "question_allergy_pear";

    public static final String QUESTION_AGE_OVER_18 = "question_age_over_18";
    public static final String QUESTION_PREGNANCY = "question_pregnancy";

    //Rejections
    public static final String ALLERGY_REJECT = "We cannot prescribe this medication because of your allergy.";
    public static final String PREGNANCY_REJECT = "We cannot prescribe this medication if you are pregnant or breastfeeding.";
    public static final String AGE_REJECT = "You must be over 18 to use this service.";

    //Answers
    public static final String YES = "Yes";
    public static final String NO = "No";

}
