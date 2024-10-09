package org.example.currencies_converter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.currencies_converter.client.FeignClientCurrencyParser;
import org.example.currencies_converter.dto.ConversionResponse;
import org.example.currencies_converter.dto.CurrencyConversionRequest;
import org.example.currencies_converter.dto.CurrencyRateResponse;
import org.example.currencies_converter.exception.CurrencyNotFoundException;
import org.example.currencies_converter.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurrencyController.class)
public class CurrencyControllerFullTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private FeignClientCurrencyParser feignClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        cacheManager.getCache("currencyRates").clear();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCurrencyRate_ValidCode() throws Exception {
        CurrencyRateResponse response = new CurrencyRateResponse("USD", 75.0);
        when(currencyService.getCurrencyRate("USD")).thenReturn(response);

        mockMvc.perform(get("/currencies/rates/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.rate").value(75.0));
    }

    @Test
    public void testGetCurrencyRate_InvalidCode() throws Exception {
        when(currencyService.getCurrencyRate("INVALID")).thenThrow(new CurrencyNotFoundException("Currency not found"));

        mockMvc.perform(get("/currencies/rates/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Currency not found"));
    }

    @Test
    public void testConvertCurrency_Success() throws Exception {
        CurrencyConversionRequest request = new CurrencyConversionRequest("USD", "EUR", 100.0);
        ConversionResponse response = new ConversionResponse("USD", "EUR", 85.0);

        when(currencyService.convertCurrency(any(String.class), any(String.class), any(Double.class)))
                .thenReturn(85.0);

        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCurrency").value("USD"))
                .andExpect(jsonPath("$.toCurrency").value("EUR"))
                .andExpect(jsonPath("$.convertedAmount").value(85.0));
    }

    @Test
    public void testConvertCurrency_InvalidAmount() throws Exception {
        CurrencyConversionRequest request = new CurrencyConversionRequest("USD", "EUR", -100.0);

        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Amount must be greater than zero"));
    }

    @Test
    public void testConvertCurrency_ServiceUnavailable() throws Exception {
        CurrencyConversionRequest request = new CurrencyConversionRequest("USD", "EUR", 100.0);

        when(currencyService.convertCurrency(any(String.class), any(String.class), any(Double.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value(503))
                .andExpect(jsonPath("$.message").value("Service unavailable"));
    }

    @Test
    public void testConvertCurrency_EmptyFromCurrency() throws Exception {
        CurrencyConversionRequest request = new CurrencyConversionRequest("", "EUR", 100.0);

        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("From currency must not be blank"));
    }

    @Test
    public void testConvertCurrency_EmptyToCurrency() throws Exception {
        CurrencyConversionRequest request = new CurrencyConversionRequest("USD", "", 100.0);

        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("To currency must not be blank"));
    }

    @Test
    public void testConvertCurrency_ZeroAmount() throws Exception {
        CurrencyConversionRequest request = new CurrencyConversionRequest("USD", "EUR", 0.0);

        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Amount must be greater than zero"));
    }

    @Test
    public void testConvertCurrency_NonExistentCurrency() throws Exception {
        CurrencyConversionRequest request = new CurrencyConversionRequest("USD", "XYZ", 100.0);

        when(currencyService.convertCurrency(any(String.class), any(String.class), any(Double.class)))
                .thenThrow(new CurrencyNotFoundException("Unsupported currency code: XYZ"));

        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Unsupported currency code: XYZ"));
    }

    @Test
    public void testGetCurrencyRate_EmptyCode() throws Exception {
        mockMvc.perform(get("/currencies/rates/"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Currency code must not be empty"));
    }
    @Test
    public void testFetchCurrencyRates_ServiceUnavailable() throws Exception {
        when(feignClient.getCurrencyRates(anyString())).thenThrow(new RuntimeException("Service unavailable"));
        mockMvc.perform(get("/currencies/rates/USD"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value(503))
                .andExpect(jsonPath("$.message").value("Currency service is currently unavailable"));
    }
}