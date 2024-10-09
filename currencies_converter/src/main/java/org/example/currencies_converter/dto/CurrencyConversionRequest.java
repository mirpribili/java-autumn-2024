package org.example.currencies_converter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyConversionRequest {
    private String fromCurrency;
    private String toCurrency;
    private Double amount;
}
