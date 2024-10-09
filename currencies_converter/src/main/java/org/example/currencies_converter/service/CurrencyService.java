package org.example.currencies_converter.service;

import lombok.extern.slf4j.Slf4j;
import org.example.currencies_converter.client.FeignClientCurrencyParser;
import org.example.currencies_converter.dto.CurrencyData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import org.xml.sax.InputSource;

@Slf4j
@Service
public class CurrencyService {

    private final FeignClientCurrencyParser feignClient;

    @Autowired
    public CurrencyService(FeignClientCurrencyParser feignClient) {
        this.feignClient = feignClient;
    }

    @Cacheable("currencyRates")
    public Set<CurrencyData> fetchCurrencyRates() {
        Set<CurrencyData> currencySet = new HashSet<>();
        try {
            Map<String, String> currencyMap = new HashMap<>();
            // Получаем текущую дату в формате dd/MM/yyyy
            String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

            // Получаем XML как строку через Feign Client с текущей датой
            String xmlResponse = feignClient.getCurrencyRates(currentDate);

            // Парсим XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlResponse)));

            // Получаем список валют
            NodeList valuteList = document.getElementsByTagName("Valute");

            for (int i = 0; i < valuteList.getLength(); i++) {
                Element valute = (Element) valuteList.item(i);

                String numCode = valute.getElementsByTagName("NumCode").item(0).getTextContent();
                String charCode = valute.getElementsByTagName("CharCode").item(0).getTextContent();
                int nominal = Integer.parseInt(valute.getElementsByTagName("Nominal").item(0).getTextContent());
                String name = valute.getElementsByTagName("Name").item(0).getTextContent();

                // Парсим vunitRate
                BigDecimal value = new BigDecimal(valute.getElementsByTagName("Value").item(0).getTextContent().replace(",", "."));
                BigDecimal vunitRate = new BigDecimal(valute.getElementsByTagName("VunitRate").item(0).getTextContent().replace(",", "."));

                CurrencyData currencyData = new CurrencyData(numCode, charCode, nominal, name, value, vunitRate);
                currencySet.add(currencyData);

                log.info("Валюта: {} ({}): {} рублей", name, charCode, value);
            }
        } catch (Exception e) {
            log.error("Error fetching currency rates", e);
        }
        return currencySet;
    }
}