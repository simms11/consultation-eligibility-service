package com.medexpress.consultation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

    @Id
    private String id;
    private String name;
    private boolean active;

    public Product() {}

    public Product(String id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isActive() { return active; }
}