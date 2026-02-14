package com.medexpress.consultation.config;

import com.medexpress.consultation.model.ProductId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class StringToProductIdConverter implements Converter<String, ProductId> {

    @Override
    public ProductId convert(String source) {
        return ProductId.fromString(source);
    }
}