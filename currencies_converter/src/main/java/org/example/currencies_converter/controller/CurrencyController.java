package org.example.currencies_converter.controller;

import jakarta.validation.Valid;
import org.example.currencies_converter.dto.ConversionResponse;
import org.example.currencies_converter.dto.CurrencyConversionRequest;
import org.example.currencies_converter.dto.CurrencyRateResponse;
import org.example.currencies_converter.dto.ErrorResponse;
import org.example.currencies_converter.exception.CurrencyNotFoundException;
import org.example.currencies_converter.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/currencies")
@Validated  // Добавьте эту аннотацию
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
        try {
            double convertedAmount = currencyService.convertCurrency(request.getFromCurrency(), request.getToCurrency(), request.getAmount());
            return ResponseEntity.ok(new ConversionResponse(request.getFromCurrency(), request.getToCurrency(), convertedAmount));
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Null value encountered in conversion"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (CurrencyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Retry-After", "3600")
                    .body(new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Service unavailable"));
        }
    }


    // Обработчик ошибок валидации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }
}