package org.example.currencies_converter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyConversionRequest {
    @NotBlank(message = "From currency must not be blank")
    private String fromCurrency;

    @NotBlank(message = "To currency must not be blank")
    private String toCurrency;

    @Positive(message = "Amount must be greater than zero")
    @NotBlank(message = "To amount must not be blank")
    private Double amount;
}