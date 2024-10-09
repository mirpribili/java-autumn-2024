package org.example.currencies_converter.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyRateRequest {
    @NotBlank(message = "Currency code must not be empty")
    private String code;
}
