package com.medexpress.consultation.config;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.medexpress.consultation.model.ProductId;


public class ProductIdConverter extends StdConverter<String, ProductId> {
    @Override
    public ProductId convert(String value) {
        return ProductId.fromString(value);
    }
}