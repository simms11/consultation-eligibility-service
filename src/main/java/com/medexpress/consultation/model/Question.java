package com.medexpress.consultation.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import jakarta.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "questions", indexes = {@Index(name = "idx_question_product_id", columnList = "productId")})
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Question {

    @Id
    private String id;
    private String productId;
    private String text;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> options;

    private String expectedAnswer;
    private String rejectedMessage;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_outcomes", joinColumns = @JoinColumn(name = "question_id"))
    @MapKeyColumn(name = "answer")
    @Column(name = "message")
    private Map<String, String> specificOutcomes = new HashMap<>();

    public Question() {}

    public Question(String id, String productId, String text, List<String> options,
                    String expectedAnswer, String rejectedMessage, Map<String, String> specificOutcomes) {
        this.id = id;
        this.productId = productId;
        this.text = text;
        this.options = options;
        this.expectedAnswer = expectedAnswer;
        this.rejectedMessage = rejectedMessage;
        this.specificOutcomes = specificOutcomes != null ? specificOutcomes : new HashMap<>();
    }

    public String id() { return id; }
    public String productId() { return productId; }
    public String text() { return text; }
    public List<String> options() { return options; }
    public String expectedAnswer() { return expectedAnswer; }
    public String rejectedMessage() { return rejectedMessage; }
    public Map<String, String> specificOutcomes() { return specificOutcomes; }
}