package org.example.currencies_converter.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.example.currencies_converter.client.FeignClientCurrencyParser;
import org.example.currencies_converter.dto.CurrencyData;
import org.example.currencies_converter.dto.CurrencyRateResponse;
import org.example.currencies_converter.exception.CurrencyNotFoundException;
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
    public Set<CurrencyData> fetchCurrencyRates() throws ParserConfigurationException, IOException, SAXException {
        Set<CurrencyData> currencySet = new HashSet<>();
        try {
            String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            String xmlResponse = feignClient.getCurrencyRates(currentDate);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlResponse)));

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
        } catch (Exception e) {
            log.error("Error fetching currency rates", e);
            throw e; // Важно выбросить исключение для регистрации в Circuit Breaker
        }
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
        } catch (IOException e) {
            log.error("Ошибка ввода-вывода: {}", e.getMessage());
            throw new RuntimeException("Ошибка ввода-вывода", e);
        } catch (SAXException e) {
            log.error("Ошибка парсинга XML: {}", e.getMessage());
            throw new RuntimeException("Ошибка парсинга XML", e);
        } catch (Exception e) {
            log.error("Неизвестная ошибка: {}", e.getMessage());
            throw new RuntimeException("Неизвестная ошибка", e);
        }
    }

    public double convertCurrency(String fromCurrency, String toCurrency, double amount) throws CurrencyNotFoundException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        // Получаем все валютные данные
        Set<CurrencyData> currencyRates = null;
        try {
            currencyRates = fetchCurrencyRates();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        // Находим данные для исходной и целевой валюты
        CurrencyData fromRate = currencyRates.stream()
                .filter(currency -> currency.getCharCode().equalsIgnoreCase(fromCurrency))
                .findFirst()
                .orElseThrow(() -> new CurrencyNotFoundException("Unsupported currency code: " + fromCurrency));

        CurrencyData toRate = currencyRates.stream()
                .filter(currency -> currency.getCharCode().equalsIgnoreCase(toCurrency))
                .findFirst()
                .orElseThrow(() -> new CurrencyNotFoundException("Unsupported currency code: " + toCurrency));

        // Конвертация: (amount / fromRate.value) * toRate.value
        return (amount / fromRate.getValue().doubleValue()) * toRate.getValue().doubleValue();
    }

    public void simulateFailure() {
        throw new RuntimeException("Simulated failure for testing");
    }
}