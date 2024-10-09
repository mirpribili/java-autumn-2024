package org.example.currencies_converter.cache;


import lombok.extern.slf4j.Slf4j;
import org.example.currencies_converter.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CacheInitializer implements ApplicationRunner {

    private final CurrencyService currencyService;

    @Autowired
    public CacheInitializer(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            log.info("Attempting to populate currency rates cache at startup...");
            var var = currencyService.fetchCurrencyRates();
            log.info("Currency rates cache has been populated at startup.");
        } catch (Exception e) {
            log.error("Failed to populate currency rates cache at startup", e);
        }
    }
}