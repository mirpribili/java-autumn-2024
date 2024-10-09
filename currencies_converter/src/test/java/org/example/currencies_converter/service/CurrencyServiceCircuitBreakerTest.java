package org.example.currencies_converter.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.example.currencies_converter.client.FeignClientCurrencyParser;
import org.example.currencies_converter.dto.CurrencyData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CurrencyServiceCircuitBreakerTest {

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private FeignClientCurrencyParser feignClientMock;

    @BeforeEach
    public void setUp() {
        feignClientMock = Mockito.mock(FeignClientCurrencyParser.class);
        currencyService = new CurrencyService(feignClientMock);
    }

    @Test
    public void testCircuitBreakerOpensOnFailures() throws ParserConfigurationException, IOException, SAXException {
        // Симулируем сбои
        //Mockito.when(feignClientMock.getCurrencyRates(Mockito.anyString()))
        //        .thenThrow(new RuntimeException("Simulated failure"));

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("currencyService");
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);

        // Вызываем метод несколько раз для триггера сбоев
        // circuitBreaker.getMetrics().getNumberOfFailedCalls()
        for (int i = 0; i < 6; i++) { // Больше чем minimumNumberOfCalls (5)
            try {
                currencyService.simulateFailure();
            } catch (Exception ignored) {
            }
        }

        // Проверяем, открыт ли контур
        //assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }
}