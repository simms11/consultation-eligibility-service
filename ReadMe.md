# MedExpress Genovia - Consultation API

This is a Spring Boot Patient Consultation Eligibility Service. The application validates patient eligibility for medical treatments through a configurable question-and-answer system.

## Project Overview

This service provides a RESTful API to:

- Retrieve product-specific medical questionnaires
- Validate patient answers against clinical eligibility rules
- Determine patient eligibility with configurable business rules
- Support multiple products through database-driven configuration

## Tech Stack

- **Java 21**
- **Spring Boot 3.2.2**
- **Spring Data JPA**
- **H2 Database**
- **JUnit 5 & Mockito**
- **Jakarta Validation**
- **Lombok**
- **Maven**

## Setup & Running

### Prerequisites

- Java 17 or higher
- Maven

### Run Locally

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

### Run Tests

```bash
mvn test
```

## API Usage

### 1. Get Questions by Product

```bash
curl -X GET http://localhost:8080/consultation/products/genovian_pear/questions
```

**Response Example:**
```json
[
  {
    "id": "question_allergy_pear",
    "productId": "genovian_pear",
    "text": "Have you been diagnosed with an allergy to Genovian Pears?",
    "options": ["Yes", "No"]
  },
  {
    "id": "question_age_over_18",
    "productId": "genovian_pear",
    "text": "Are you over 18 years of age?",
    "options": ["Yes", "No"]
  },
  {
    "id": "question_pregnancy",
    "productId": "genovian_pear",
    "text": "Are you currently pregnant or breastfeeding?",
    "options": ["Yes", "No"]
  },
  {
    "id": "question_blood_pressure",
    "productId": "genovian_pear",
    "text": "How would you describe your blood pressure?",
    "options": ["Low", "Normal", "High"]
  }
]
```

### 2. Submit Consultation (Eligibility Check)

#### Scenario A: Eligible Patient

- Not allergic to Genovian Pears
- Over 18
- Not pregnant
- Normal blood pressure

```bash
curl -X POST http://localhost:8080/consultation/submit \
-H "Content-Type: application/json" \
-d '{
    "productId": "genovian_pear",
    "answers": [
        { "questionId": "question_allergy_pear", "answer": "No" },
        { "questionId": "question_age_over_18", "answer": "Yes" },
        { "questionId": "question_pregnancy", "answer": "No" },
        { "questionId": "question_blood_pressure", "answer": "Normal" }
    ]
}'
```

**Response:**
```json
{
  "eligible": true,
  "message": null
}
```

#### Scenario B: Ineligible Patient (Has Allergy)

Patient has the allergy that this medication treats.

```bash
curl -X POST http://localhost:8080/consultation/submit \
-H "Content-Type: application/json" \
-d '{
    "productId": "genovian_pear",
    "answers": [
        { "questionId": "question_allergy_pear", "answer": "Yes" },
        { "questionId": "question_age_over_18", "answer": "Yes" },
        { "questionId": "question_pregnancy", "answer": "No" },
        { "questionId": "question_blood_pressure", "answer": "Normal" }
    ]
}'
```

**Response:**
```json
{
  "eligible": false,
  "message": "This medication is only prescribed for patients with Genovian Pear allergy."
}
```

#### Error Handling Examples

**Missing Required Field:**
```json
{
  "timestamp": "2026-02-14T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "details": {
    "productId": "Product ID is required"
  }
}
```

**Invalid Product ID:**
```json
{
  "timestamp": "2026-02-14T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Unknown product: invalid_product"
}
```

**Wrong Number of Answers:**
```json
{
  "timestamp": "2026-02-14T10:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Expected: 4 answers"
}
```

**Invalid Answer Option:**
```json
{
  "timestamp": "2026-02-14T10:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Invalid answer 'Maybe' for question question_allergy_pear. Valid options: [Yes, No]"
}
```

## Validation Rules

The service enforces comprehensive validation at multiple layers:

### Request-Level Validation (Jakarta Validation)

1. **Product ID**: Required, cannot be null
2. **Answers List**: Required, cannot be null or empty
3. **Question ID**: Required for each answer, cannot be blank
4. **Answer Value**: Required for each answer, cannot be blank

### Business-Level Validation (Service Layer)

5. **Product Existence**: Product ID must exist in database
6. **Answer Count**: Must match the number of questions for the product
7. **No Duplicates**: Each question can only be answered once
8. **Valid Question IDs**: All question IDs must belong to the specified product
9. **Valid Answer Options**: Answers must match one of the predefined options for each question

Any validation failure returns a `400 Bad Request` with a descriptive error message.

## Design Decisions & Trade-offs

### Multi-Product Architecture

Questions are mapped to products via `productId` with JPA relationships. Database-driven configuration enables new products to be added by updating `questions.json` without code changes.

### Configurable Business Rules Engine

The `specificOutcomes` mapping on Question entity eliminates hardcoded business rules. Special cases are configured in the database rather than code.

### Type-Safe Product Identifiers

`ProductId` enum with Jackson converter provides compile-time validation. Invalid product IDs are caught at JSON deserialization.

### Security Through DTOs

`QuestionDTO` exposes only public fields in API responses, preventing clients from seeing expected answers or business rules.

### Structured Logging

SLF4J logging throughout the service layer provides production observability.

### Areas for Future Enhancement

1. Integration Testing

2. Production Database

3. Audit Trail

4. API Documentation

 5. Spring Boot Actuator for observability & monitoring