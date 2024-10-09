package org.example.currencies_converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CurrenciesConverterApplication {
	public static void main(String[] args) {
		SpringApplication.run(CurrenciesConverterApplication.class, args);
	}
}