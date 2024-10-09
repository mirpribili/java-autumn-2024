package org.example.currencies_converter.temp;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class CurrencyParser {
    private static final String URL_STRING = "http://www.cbr.ru/scripts/XML_daily.asp?date_req=07/10/2024";

    public static void main(String[] args) {
        try {
            // Создаем объект URL
            URL url = new URL(URL_STRING);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Проверяем код ответа
            if (connection.getResponseCode() == 200) {
                // Получаем InputStream
                InputStream inputStream = connection.getInputStream();

                // Создаем DocumentBuilder для парсинга XML
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(inputStream);

                // Получаем список валют
                NodeList valuteList = document.getElementsByTagName("Valute");

                for (int i = 0; i < valuteList.getLength(); i++) {
                    Element valute = (Element) valuteList.item(i);
                    String charCode = valute.getElementsByTagName("CharCode").item(0).getTextContent();
                    String name = valute.getElementsByTagName("Name").item(0).getTextContent();
                    String value = valute.getElementsByTagName("Value").item(0).getTextContent();

                    System.out.println("Валюта: " + name + " (" + charCode + ") - " + value + " рублей");
                }

                inputStream.close();
            } else {
                System.out.println("Ошибка: " + connection.getResponseCode());
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}