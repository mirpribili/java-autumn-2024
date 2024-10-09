package org.example.currencies_converter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConversionResponse {
    private String fromCurrency;
    private String toCurrency;
    private Double convertedAmount;
}