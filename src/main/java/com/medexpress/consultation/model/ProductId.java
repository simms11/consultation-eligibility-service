package com.medexpress.consultation.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.medexpress.consultation.config.ProductIdConverter;

@JsonDeserialize(converter = ProductIdConverter.class)
public enum ProductId {
    GENOVIAN_PEAR("genovian_pear");

    private final String value;

    ProductId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ProductId fromString(String text) {
        for (ProductId b : ProductId.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unknown product: " + text);
    }
}