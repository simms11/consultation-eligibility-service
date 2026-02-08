package com.medexpress.consultation.model;

import java.util.List;

public record Question(String id,
                       String text,
                       List<String> options,
                       String expectedAnswer,
                       String rejectedMessage){}
