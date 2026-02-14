package com.medexpress.consultation.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medexpress.consultation.model.Product;
import com.medexpress.consultation.model.Question;
import com.medexpress.consultation.repository.ProductRepository;
import com.medexpress.consultation.repository.QuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

import static com.medexpress.consultation.constant.ConsultationConstants.Products.GENOVIAN_PEAR;

@Slf4j
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(QuestionRepository questionRepo, ProductRepository productRepo) {
        return args -> {

            if (productRepo.count() == 0) {
                productRepo.save(new Product(GENOVIAN_PEAR, "Genovian Pear", true));
                log.info("Seeded default product: {}", GENOVIAN_PEAR);
            }

            if (questionRepo.count() == 0) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    InputStream inputStream = new ClassPathResource("questions.json").getInputStream();
                    List<Question> questions = mapper.readValue(inputStream, new TypeReference<>() {});

                    questionRepo.saveAll(questions);
                    log.info("Database seeded with {} questions from questions.json", questions.size());
                } catch (Exception e) {
                    log.error("Failed to seed database", e);
                }
            }
        };
    }
}