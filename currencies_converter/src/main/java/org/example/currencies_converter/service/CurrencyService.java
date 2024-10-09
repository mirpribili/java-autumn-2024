package org.example.currencies_converter.service;

import org.example.currencies_converter.client.FeignClientCurrencyParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.xml.sax.InputSource;

@Service
public class CurrencyService {

    private final FeignClientCurrencyParser feignClient;

    @Autowired
    public CurrencyService(FeignClientCurrencyParser feignClient) {
        this.feignClient = feignClient;
    }

    public void fetchCurrencyRates() {
        try {
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
                String charCode = valute.getElementsByTagName("CharCode").item(0).getTextContent();
                String name = valute.getElementsByTagName("Name").item(0).getTextContent();
                String value = valute.getElementsByTagName("Value").item(0).getTextContent();

                System.out.println("Валюта: " + name + " (" + charCode + ") - " + value + " рублей");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}