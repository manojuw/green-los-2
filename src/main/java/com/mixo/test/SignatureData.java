package com.mixo.test;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class SignatureData {

	@XmlElement(name = "SignatureValue")
	private String signatureValue;

	// Getters and setters
}
