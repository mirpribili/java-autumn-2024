package ru.tbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

@Data
@XmlRootElement(name = "city")
@XmlAccessorType(XmlAccessType.FIELD)
public class City {
    @JsonProperty("slug")
    @XmlElement(name = "slug")
    private String slug;

    @JsonProperty("coords")
    @XmlElement(name = "coordinates")
    private Coordinates coords;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Coordinates {
        @JsonProperty("lat")
        @XmlElement(name = "latitude")
        private double latitude;

        @JsonProperty("lon")
        @XmlElement(name = "longitude")
        private double longitude;
    }

    public String toXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<city>\n" +
                "  <slug>" + slug + "</slug>\n" +
                "  <coordinates>\n" +
                "    <latitude>" + coords.getLatitude() + "</latitude>\n" +
                "    <longitude>" + coords.getLongitude() + "</longitude>\n" +
                "  </coordinates>\n" +
                "</city>";
    }

    public void saveToXML(String filePath) {
        try {
            Files.write(Paths.get(filePath), toXML().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Error saving XML to file: " + filePath, e);
        }
    }
}
