# MedExpress Genovia - Consultation API

This is a Spring Boot MVP Patient Consultation Eligibility Service. The application confirms treatment for the Genovian Pear Allergy.

##  Project Overview

This service provides a RESTful API to:

- Retrieve the specific medical questionnaire for the Genovian Pear condition.
- Validate patient answers against clinical eligibility rules.
- Determine if a patient is eligible for the prescribed treatment.

## ️ Tech Stack

- **Java 17+**
- **Spring Boot**
- **JUnit 5 & Mockito**
- **Maven**

## ️ Setup & Running

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
## API Testing

A Postman collection has been included in this repository.

[ Download Postman Collection](./postman/MedExpress_Collection.json)

To use:
1. Download the file above.
2. Open Postman.
3. Click **Import** > **Upload Files**.
4. Select `MedExpress_Collection.json`.


##  API Usage

### 1. Get Questions

Retrieves the list of questions for the default product (Genovian Pear).

```bash
curl -X GET http://localhost:8080/consultation/questions
```

**Response Example:**
```json
[
  {
    "id": "question_allergy_pear",
    "text": "Have you been diagnosed with an allergy to Genovian Pears?",
    "options": ["Yes", "No"],
    "expectedAnswer": "Yes",
    "rejectedMessage": "We cannot prescribe this medication because of your allergy."
  },
  {
    "id": "question_age_over_18",
    "text": "Are you over 18 years of age?",
    "options": ["Yes", "No"],
    "expectedAnswer": "Yes",
    "rejectedMessage": "You must be over 18 to use this service."
  },
  {
    "id": "question_pregnancy",
    "text": "Are you currently pregnant or breastfeeding?",
    "options": ["Yes", "No"],
    "expectedAnswer": "No",
    "rejectedMessage": "We cannot prescribe this medication if you are pregnant or breastfeeding."
  }
]
```

### 2. Submit Consultation (Eligibility Check)

#### Scenario A: Eligible Patient

- Has the allergy (Prerequisite for treatment)
- Over 18
- Not pregnant

```bash
curl -X POST http://localhost:8080/consultation/submit \
-H "Content-Type: application/json" \
-d '{
    "answers": [
        { "questionId": "question_allergy_pear", "answer": "Yes" }, 
        { "questionId": "question_age_over_18", "answer": "Yes" },
        { "questionId": "question_pregnancy", "answer": "No" }
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

#### Scenario B: Ineligible Patient

- Does not have the allergy (Treatment not required)

```bash
curl -X POST http://localhost:8080/consultation/submit \
-H "Content-Type: application/json" \
-d '{
    "answers": [
        { "questionId": "question_allergy_pear", "answer": "No" },
        { "questionId": "question_age_over_18", "answer": "Yes" },
        { "questionId": "question_pregnancy", "answer": "No" }
    ]
}'
```

**Response:**
```json
{
  "eligible": false,
  "message": "We cannot prescribe this medication because of your allergy."
}
```

#### Error Handling

Invalid submissions will return a 400 Bad Request with details:

```json
{
  "timestamp": "2026-02-08T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Expected: 3 answers"
}
```

##  Validation Rules

The service enforces the following validation rules on submission:

1. **Answer Count:** 
2. **No Duplicates:**
3. **Valid Question IDs:**
4. **Valid Answer Options:** ("Yes" or "No")

Any validation failure returns a `400 Bad Request` with a descriptive error message.

##  Design Decisions & Trade-offs

### 1. Domain Logic

I interpreted the requirement *"The condition we have chosen to target is an allergy to... the Genovian Pear"* as a treatment scenario.

- **Decision:** A patient must have the allergy (question_allergy_pear = Yes) to be eligible.
- **Reasoning:** The allergy is the condition being treated. If a patient is not allergic, they do not need the medicine.

### 2. Architecture: Service-Layer Validation

- **Decision:** Validation is performed in the `ConsultationService`.
- **Reasoning:** This encapsulates business rules within the domain layer. Data integrity rules are always enforced, allowing the Controller to strictly focus on HTTP concerns.

### 3. Records & Immutability

- **Decision:** Used Java `record` types (`SubmitConsultationRequest`, `EligibilityResponse`, `Question`, `ConsultationAnswer`) instead of Lombok or POJOs.
- **Reasoning:** This provides immutability whilst reduces boilerplate.

### 4. Storage

- **Decision:** Used a `HashMap` in the Repository layer as no permanent storage is required.

##  Areas for Improvement

Given more time, I would implement the following extensions:

### 1. Code Quality & Robustness

- **UUIDs:** Transition from `String` IDs to `UUID` for the `consultationId` to ensure global uniqueness.
- **Strong Typing:** Refactor the `SubmitConsultationRequest` to use Enums (e.g., `AnswerType.YES`) instead of Strings, preventing casing/whitespace bugs.

### 2. Product Catalog

- **Improvement:** Refactor the Repository to map questions to a Product ID.
- **Plan:** Update the API to accept `GET /questions?productId=acne`. This allows new products to be added.

### 3. Data Storage

- **Improvement:** Implement a hybrid database strategy.
- **Plan:**
    - **NoSQL (MongoDB):** For Consultation Answers (flexible schema, varies by product).
    - **SQL (PostgreSQL):** For User & Payment data (ACID compliance).

### 5. Audit Logging & Pagination

- **Context:** Given the "Genovian Pear" is a critical export, the system requires high reliability and auditability.
- **Plan:** Implement Cursor-Based Pagination for the Doctor's Dashboard and Audit Logging to defend decisions against regulatory audits.