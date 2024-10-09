package org.example.currencies_converter.controller;

import jakarta.validation.Valid;
import org.example.currencies_converter.dto.ConversionResponse;
import org.example.currencies_converter.dto.CurrencyConversionRequest;
import org.example.currencies_converter.dto.CurrencyRateResponse;
import org.example.currencies_converter.dto.ErrorResponse;
import org.example.currencies_converter.exception.CurrencyNotFoundException;
import org.example.currencies_converter.exception.CurrencyServiceUnavailableException;
import org.example.currencies_converter.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/currencies")
@Validated
public class CurrencyController {

    private final CurrencyService currencyService;

    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Operation(summary = "Получить курс валюты по коду",
            description = "Возвращает текущий курс валюты по указанному коду. Если валюта не найдена, возвращает код ошибки NOT_FOUND.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Курс валюты успешно получен",
                            content = @Content(examples = @ExampleObject(value = "{\"code\":\"USD\", \"rate\":75.0}"))),
                    @ApiResponse(responseCode = "404", description = "Валюта не найдена",
                            content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"Currency not found\"}")))
            })
    @GetMapping("/rates/{code}")
    public ResponseEntity<?> getCurrencyRate(
            @Parameter(description = "Код валюты, для которой требуется получить курс", required = true)
            @Valid @PathVariable String code) {
        try {
            // Получаем курс валюты
            CurrencyRateResponse response = currencyService.getCurrencyRate(code);
            return ResponseEntity.ok(response);
        } catch (CurrencyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (CurrencyServiceUnavailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "Currency service is currently unavailable"));
        } catch (RuntimeException e) {
            // Обработка RuntimeException
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred"));
        }
    }

    @GetMapping({"/rates/", "/rates"})
    public ResponseEntity<ErrorResponse> getCurrencyRateWithoutCode() {
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Currency code must not be empty"));
    }

    @Operation(summary = "Конвертировать валюту",
            description = "Конвертирует указанную сумму из одной валюты в другую. Убедитесь, что сумма больше нуля и валюта поддерживается.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Запрос на конвертацию валюты",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = "{\"fromCurrency\":\"USD\", \"toCurrency\":\"EUR\", \"amount\":150}"), // Пример успешного запроса
                                    @ExampleObject(value = "{\"fromCurrency\":\"USD\", \"toCurrency\":\"XYZ\", \"amount\":50}"), // Пример с ошибкой (несуществующая валюта)
                                    @ExampleObject(value = "{\"fromCurrency\":\"EUR\", \"toCurrency\":\"RUB\", \"amount\":-100}") // Пример с ошибкой (отрицательная сумма)
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Конвертация выполнена успешно",
                            content = @Content(examples = @ExampleObject(value = "{\"fromCurrency\":\"USD\", \"toCurrency\":\"EUR\", \"convertedAmount\":135.0}"))),
                    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                            content = @Content(examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"Amount must be greater than zero\"}"))),
                    @ApiResponse(responseCode = "404", description = "Валюта не найдена",
                            content = @Content(examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"Unsupported currency code: XYZ\"}"))),
                    @ApiResponse(responseCode = "503", description = "Сервис недоступен",
                            content = @Content(examples = @ExampleObject(value = "{\"code\": 503, \"message\": \"Service unavailable\"}")))
            })
    @PostMapping("/convert")
    public ResponseEntity<?> convertCurrency(
            @Valid
            @Parameter(description = "Запрос на конвертацию валюты. Пример: {\"fromCurrency\":\"USD\", \"toCurrency\":\"EUR\", \"amount\":100}", required = true)
            @RequestBody CurrencyConversionRequest request) {
        try {
            double convertedAmount =
                    currencyService.convertCurrency(request.getFromCurrency(), request.getToCurrency(), request.getAmount());
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
        String errorMessage =
                ex.getBindingResult().getFieldErrors()
                        .stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage));
    }

    @ExceptionHandler(CurrencyServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleCurrencyServiceUnavailable(CurrencyServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), ex.getMessage()));
    }
}