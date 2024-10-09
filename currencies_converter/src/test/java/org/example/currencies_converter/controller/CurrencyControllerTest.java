package org.example.currencies_converter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.currencies_converter.dto.ConversionResponse;
import org.example.currencies_converter.dto.CurrencyConversionRequest;
import org.example.currencies_converter.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurrencyController.class)
public class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testConvertCurrency() throws Exception {
        // Подготовка данных для теста
        CurrencyConversionRequest request = new CurrencyConversionRequest("USD", "RUB", 100.5);
        ConversionResponse response = new ConversionResponse("USD", "RUB", 9000.5);

        // Настройка мока
        when(currencyService.convertCurrency(any(String.class), any(String.class), any(Double.class)))
                .thenReturn(9000.5); // Устанавливаем ожидаемое значение

        // Выполнение запроса и проверка ответа
        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCurrency").value("USD"))
                .andExpect(jsonPath("$.toCurrency").value("RUB"))
                .andExpect(jsonPath("$.convertedAmount").value(9000.5));
    }
}