package com.medexpress.consultation.repository;

import com.medexpress.consultation.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
