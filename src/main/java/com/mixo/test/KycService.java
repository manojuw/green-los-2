package com.mixo.test;

import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.stereotype.Service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KycService {

	public String generateSignedXML(String privateKeyPem) throws Exception {
		// Create Head
		Head head = new Head();
		head.setMsgId("HML12345675565273665474887");
		head.setOrgId("200374");
		head.setProdType("AEPS");
		head.setTimestamp("2024-09-02T15:46:42.774+05:30");
		head.setVersion("2.0");

		// Create Txn
		Txn txn = new Txn();
		txn.setId("HML12345675565273665474887");
		txn.setNote("Testing Request API");
		txn.setRefId("202771");
		txn.setRefUrl("https://www.npci.org.in/");
		txn.setTimestamp("2024-09-02T15:46:42.774+05:30");
		txn.setType("KycData");

		// Create ReqKycData
		ReqKycData reqKycData = new ReqKycData();
		reqKycData.setHead(head);
		reqKycData.setTxn(txn);

		// Convert to XML
		String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><ns2:ReqKycData xmlns:ns2=\"http://npci.org/upi/schema/\">\r\n"
				+ " <Head msgId=\"HML12345675565273665474887\" orgId=\"202771\" prodType=\"AEPS\" ts=\""
				+ LocalDateTime.now() + "\" ver=\"2.0\"/>\r\n"
				+ " <Txn id=\"HML12345675565273665474887\" note=\"Testing Request API\" refId=\"474887\" refUrl=\"https://www.npci.org.in/\" ts=\""
				+ LocalDateTime.now() + "\" type=\"KycData\">\r\n" + " <RiskScores/>\r\n" + " </Txn>\r\n"
				+ "</ns2:\r\n"
				+ "ReqKycData>>";

		// Sign XML
		String signatureValue = signData(xmlData, privateKeyPem);
//		SignatureData signature = new SignatureData();
//		signature.setSignatureValue(signatureValue);
//		reqKycData.setSignature(signature);

		return signatureValue+xmlData;
	}

	private String marshalToXml(Object object) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(object.getClass());
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter writer = new StringWriter();
		marshaller.marshal(object, writer);
		return writer.toString();
	}

	private String signData(String data, String privateKeyPem) throws Exception {
		byte[] keyBytes = Base64.getDecoder().decode(privateKeyPem.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", ""));
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(privateKey);
		signature.update(data.getBytes());

		return Base64.getEncoder().encodeToString(signature.sign());
	}
}
