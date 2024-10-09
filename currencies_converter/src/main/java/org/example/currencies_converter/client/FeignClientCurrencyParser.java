package org.example.currencies_converter.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "currency-client", url = "${feign.client.config.currencyClient.url}")
public interface FeignClientCurrencyParser {

    @GetMapping("${feign.client.config.currencyClient.endpoint}")
    String getCurrencyRates(@RequestParam("date_req") String date);
}