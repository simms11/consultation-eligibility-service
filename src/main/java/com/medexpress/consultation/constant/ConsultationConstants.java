package com.medexpress.consultation.constant;

public class ConsultationConstants {

    private ConsultationConstants() {}

    public static class Products {
        public static final String GENOVIAN_PEAR = "genovian_pear";
    }

    public static class Questions {
        public static final String ALLERGY_PEAR = "question_allergy_pear";
        public static final String BLOOD_PRESSURE = "question_blood_pressure";
        public static final String AGE_OVER_18 = "question_age_over_18";
        public static final String PREGNANCY = "question_pregnancy";
    }

    public static class Messages {
        public static final String ALLERGY_REJECT = "We cannot prescribe this medication because of your allergy.";
        public static final String PREGNANCY_REJECT = "We cannot prescribe this medication if you are pregnant or breastfeeding.";
        public static final String AGE_REJECT = "You must be over 18 to use this service.";
        public static final String BP_HIGH_REJECT = "Your blood pressure is too high for this medication.";
        public static final String BP_LOW_CONTACT = "Please contact Customer Support to schedule an appointment.";
    }

    public static class Answers {
        public static final String YES = "Yes";
        public static final String NO = "No";
        public static final String LOW = "Low";
        public static final String NORMAL = "Normal";
        public static final String HIGH = "High";
    }
}