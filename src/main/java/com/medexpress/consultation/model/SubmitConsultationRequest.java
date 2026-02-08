package com.medexpress.consultation.model;

import java.util.List;

public record SubmitConsultationRequest(List<ConsultationAnswer> answers) {}
