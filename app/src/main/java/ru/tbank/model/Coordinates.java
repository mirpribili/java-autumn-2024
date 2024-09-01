package ru.tbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinates {
    @JsonProperty("lat")
    @XmlElement(name = "latitude")
    private double latitude;

    @JsonProperty("lon")
    @XmlElement(name = "longitude")
    private double longitude;
}