package org.example.currencies_converter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CurrencyData {
    private String numCode;
    private String charCode;
    private int nominal;
    private String name;
    private BigDecimal value;
    private BigDecimal vunitRate;
}