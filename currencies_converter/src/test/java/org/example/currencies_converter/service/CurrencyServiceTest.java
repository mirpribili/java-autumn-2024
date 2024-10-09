package org.example.currencies_converter.service;

import org.example.currencies_converter.client.FeignClientCurrencyParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.example.currencies_converter")
public class CurrencyServiceTest {

    @Autowired
    private CurrencyService currencyService;

    public static void main(String[] args) {
        SpringApplication.run(CurrencyServiceTest.class, args);
    }

    @Bean
    CommandLineRunner run() {
        return args -> {
            System.out.println("Fetching currency rates...");
            currencyService.fetchCurrencyRates();
        };
    }
}