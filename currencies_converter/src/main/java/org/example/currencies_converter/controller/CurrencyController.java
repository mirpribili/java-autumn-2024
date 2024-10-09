package org.example.currencies_converter.controller;

import org.example.currencies_converter.dto.*;
import org.example.currencies_converter.exception.CurrencyNotFoundException;
import org.example.currencies_converter.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/rates/{code}")
    public ResponseEntity<CurrencyRateResponse> getCurrencyRate(@PathVariable String code) {
        CurrencyRateResponse response = currencyService.getCurrencyRate(code);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/convert")
    public ResponseEntity<?> convertCurrency(@RequestBody CurrencyConversionRequest request) {
        if (request.getFromCurrency() == null || request.getToCurrency() == null || request.getAmount() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Missing parameters"));
        }
        if (request.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Amount must be greater than zero"));
        }

        try {
            double convertedAmount = currencyService.convertCurrency(request.getFromCurrency(), request.getToCurrency(), request.getAmount());
            return ResponseEntity.ok(new ConversionResponse(request.getFromCurrency(), request.getToCurrency(), convertedAmount));
        } catch (CurrencyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Retry-After", "3600")
                    .body(new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Service unavailable"));
        }
    }
}