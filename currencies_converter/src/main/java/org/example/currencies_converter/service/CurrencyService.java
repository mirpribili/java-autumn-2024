package org.example.currencies_converter.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.example.currencies_converter.client.FeignClientCurrencyParser;
import org.example.currencies_converter.dto.CurrencyData;
import org.example.currencies_converter.dto.CurrencyRateResponse;
import org.example.currencies_converter.exception.CurrencyNotFoundException;
import org.example.currencies_converter.exception.CurrencyServiceUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Slf4j
@Service
public class CurrencyService {

    private final FeignClientCurrencyParser feignClient;

    @Autowired
    public CurrencyService(FeignClientCurrencyParser feignClient) {
        this.feignClient = feignClient;
    }



    @Cacheable("currencyRates")
    @CircuitBreaker(name = "currencyService", fallbackMethod = "fallbackFetchCurrencyRates")
    public Set<CurrencyData> fetchCurrencyRates() {
        String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        int maxAttempts = 5; // Максимальное количество попыток
        int attempt = 0;

        while (attempt < maxAttempts) {
            CompletableFuture<Set<CurrencyData>> future = CompletableFuture.supplyAsync(() -> {
                return getCurrencyData(currentDate);
            });

            try {
                // Ожидаем завершения с таймаутом
                return future.orTimeout(5, TimeUnit.SECONDS).join(); // Таймаут 5 секунд
            } catch (CompletionException e) {
                // Проверяем, является ли причиной таймаут
                if (e.getCause() instanceof TimeoutException) {
                    log.warn("Timeout occurred on attempt {}: {}", attempt + 1, e.getMessage());
                } else {
                    log.error("Error fetching currency rates on attempt {}: {}", attempt + 1, e.getMessage());
                }
                attempt++;
            }

            // Задержка между попытками
            try {
                Thread.sleep(1000); // Задержка в 1 секунду перед следующей попыткой
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // Восстанавливаем статус прерывания
                throw new RuntimeException("Thread was interrupted", ie);
            }
        }

        throw new CurrencyServiceUnavailableException("Currency service is currently unavailable after " + maxAttempts + " attempts");
    }

    private Set<CurrencyData> getCurrencyData(String currentDate) {
        Set<CurrencyData> currencySet = new HashSet<>();

        // Выполняем внешний запрос
        String xmlResponse = feignClient.getCurrencyRates(currentDate);
        log.info("XML Response: {}", xmlResponse);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document document = null;
        try {
            document = builder.parse(new InputSource(new StringReader(xmlResponse)));
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        NodeList valuteList = document.getElementsByTagName("Valute");

        for (int i = 0; i < valuteList.getLength(); i++) {
            Element valute = (Element) valuteList.item(i);
            String numCode = valute.getElementsByTagName("NumCode").item(0).getTextContent();
            String charCode = valute.getElementsByTagName("CharCode").item(0).getTextContent();
            int nominal = Integer.parseInt(valute.getElementsByTagName("Nominal").item(0).getTextContent());
            String name = valute.getElementsByTagName("Name").item(0).getTextContent();
            BigDecimal value = new BigDecimal(valute.getElementsByTagName("Value").item(0).getTextContent().replace(",", "."));
            BigDecimal vunitRate = new BigDecimal(valute.getElementsByTagName("VunitRate").item(0).getTextContent().replace(",", "."));

            CurrencyData currencyData = new CurrencyData(numCode, charCode, nominal, name, value, vunitRate);
            currencySet.add(currencyData);

            log.info("Валюта: {} ({}): {} рублей", name, charCode, value);
        }

        // Добавляем RUB с курсом 1 к 1
        currencySet.add(new CurrencyData("643", "RUB", 1, "Российский рубль", BigDecimal.ONE, BigDecimal.ONE));

        return currencySet;
    }

    public Set<CurrencyData> fallbackFetchCurrencyRates(Throwable ex) {
        log.warn("Fallback method called due to: {}", ex.getMessage());
        return new HashSet<>(); // Возвращаем пустой набор или закэшированные данные
    }

    public CurrencyRateResponse getCurrencyRate(String code) {
        try {
            Set<CurrencyData> currencyRates = fetchCurrencyRates(); // Получаем все валюты
            CurrencyData currencyData = currencyRates.stream()
                    .filter(currency -> currency.getCharCode().equalsIgnoreCase(code)) // Сравниваем код
                    .findFirst()
                    .orElseThrow(() -> new CurrencyNotFoundException("Unsupported currency code: " + code)); // Выбрасываем исключение, если не найдено

            // Возвращаем новый объект CurrencyRateResponse с нужными данными
            return new CurrencyRateResponse(currencyData.getCharCode(), currencyData.getValue().doubleValue());
        } catch (Exception e) {
            log.error("Неизвестная ошибка: {}", e.getMessage());
            throw new RuntimeException("Неизвестная ошибка", e);
        }
    }

    public double convertCurrency(String fromCurrency, String toCurrency, double amount) throws CurrencyNotFoundException {
        // Проверка параметров
        validateCurrencyConversionRequest(fromCurrency, toCurrency, amount);

        // Получаем все валютные данные
        Set<CurrencyData> currencyRates = null;
        currencyRates = fetchCurrencyRates();

        // Находим данные для исходной и целевой валюты
        CurrencyData fromRate = currencyRates.stream()
                .filter(currency -> currency.getCharCode().equalsIgnoreCase(fromCurrency))
                .findFirst()
                .orElseThrow(() -> new CurrencyNotFoundException("Unsupported currency code: " + fromCurrency));

        CurrencyData toRate = currencyRates.stream()
                .filter(currency -> currency.getCharCode().equalsIgnoreCase(toCurrency))
                .findFirst()
                .orElseThrow(() -> new CurrencyNotFoundException("Unsupported currency code: " + toCurrency));

        // Конвертация
        if (fromCurrency.equals("RUB")) {
            return amount / toRate.getValue().doubleValue();
        } else {
            return amount * fromRate.getValue().doubleValue();
        }
    }

    public void simulateFailure() {
        throw new RuntimeException("Simulated failure for testing");
    }

    public void validateCurrencyConversionRequest(String fromCurrency, String toCurrency, Double amount) {
        if (fromCurrency == null || fromCurrency.isEmpty()) {
            throw new IllegalArgumentException("From currency must not be null or empty");
        }
        if (toCurrency == null || toCurrency.isEmpty()) {
            throw new IllegalArgumentException("To currency must not be null or empty");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }
}