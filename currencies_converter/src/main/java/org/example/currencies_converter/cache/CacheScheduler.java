package org.example.currencies_converter.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class CacheScheduler {

    private final CacheManager cacheManager;

    public CacheScheduler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Scheduled(fixedRate = 36_000) // 3_600_000
    public void checkCurrencyRatesCache() {
        Cache cache = cacheManager.getCache("currencyRates");
        if (cache != null) {
            Map<Object, Object> nativeCache = (Map<Object, Object>) cache.getNativeCache();
            if (nativeCache != null && !nativeCache.isEmpty()) {
                long count = nativeCache.values().stream()
                        .filter(value -> value instanceof Set)
                        .flatMap(value -> ((Set<?>) value).stream())
                        .count();
                log.info("The currencyRates cache contains entries: {}", count);
            } else {
                log.info("The currencyRates cache is empty.");
            }
        } else {
            log.error("The currencyRates cache does not exist.");
        }
    }

    @Scheduled(fixedRate = 3_600_000)
    @CacheEvict(value = "currencyRates", allEntries = true)
    public void evictCurrencyRatesCache() {
        log.info("Clearing currencyRates cache");
    }
}