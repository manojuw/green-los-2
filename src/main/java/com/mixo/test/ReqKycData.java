package com.mixo.test;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "ReqKycData", namespace = "http://npci.org/upi/schema/")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ReqKycData {

	@XmlElement(name = "Head", namespace = "http://npci.org/upi/schema/")
	private Head head;

	@XmlElement(name = "Txn", namespace = "http://npci.org/upi/schema/")
	private Txn txn;

	@XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
	private SignatureData signature;
}
