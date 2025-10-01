package com.mixo.test;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XMLSigner {

	public static void main(String[] args) throws Exception {
		
		 OffsetDateTime now = OffsetDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

	        String formattedDate = now.format(formatter);
		String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n"
				+ "				<ns2:ReqKycData xmlns:ns2=\"http://npci.org/upi/schema/\">\r\n"
				+ "				 <Head msgId=\"HML9X7G2W5A1Y3Z6B8C4D0EJQKRMNVTPT4K\" orgId=\"200391\" prodType=\"AEPS\"\r\n"
				+ "				 ts=\""+formattedDate+"\" ver=\"2.0\"/>\r\n"
				+ "				 <Txn id=\"HML9X7G2W5A1Y3Z6B8C4D0EJQKRMNVTPT4K\" note=\"Testing Request API\" refId=\"474887\"\r\n"
				+ "				 refUrl=\"https://www.npci.org.in/\" ts=\""+formattedDate+"\" type=\"KycData\">\r\n"
				+ "				 <RiskScores/>\r\n"
				+ "				 </Txn>\r\n"
				+ "				</ns2:ReqKycData>";

		// Load Private Key
		String privateKeyPEM = """
				-----BEGIN PRIVATE KEY-----
MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCrdnxEpw2wXMK+
HfQzBIia7v0uo83zWsCkQ+BodSReTGnE6Ix0ZVZRGqv57aMP4M0C08+o8JJ5YjxQ
LZ4IEFXGl1dBdivUZomRvPEuU+gFFnqzlG1ogT3kI6p5LJ8BcFMqGDTIrm/p5QGe
5GDjLYcuaabm6/Lcycait8hXIbNHcGEBSVl+0sZ5AaB1rR6/nVakGXyDvzLNV3c0
WVJmi11h/IA/UACm+rpmL8xP5FkCg1khqaA1621lcUlMLVXV2lbQng5upnzqQbSG
kCeElfKTRM8PnWWP7hGWvL+jxdHfLNh8qzbfYXCeRIxnQQzxKaIYrc050nrdgG/3
NL8+wo7fAgMBAAECggEAL5Zh5PfsT69fCT7tAJ/Yfg+oSyKBTXI5lx2Tkco5Psa8
cD8OhFt/umDJrELtB8IfhBJfRwcF0BSYorQWcSx/ce+c8vkmLvwKYF1tHquA8LCN
e3vNZbzA/al8bccZll+jZUJ0m+H2A5dgfMXrsgF3zETcYqjHrcl+jLivLKeYx1GP
FlwRO0BzlpoH1eWisX5aKEcjjbNbT5yDs6srCuqaN7tL0r+r+XvJ0oDuvYpUoInN
r4hGiM81SFfyG6X9lw9Mo+38foBYpNqeMIruxjn0ycUuc+XjZFXAQEreiIFtGfnI
fy88tLn15mwKzQONEQnN5pBnO0VkL7qOdaKxyrOjYQKBgQDtY/mZY6wcFT8kteev
s9KNPdQKlf8b55T/FLOnih0SEgqGXTtZvfOqBigKIwXpsBWQK1PqlJiVuRGa2Ex6
VieDPVlEib0Ys0tvQ9KKsnc6pvB8dNvgY+p/nIDHocqg+L6pynKiTNz9ISN/Rdk+
MYmWFuPNMrkcAm0TBG7TRihi7wKBgQC453QYrAmsq0NikinVIExGx6JTG6JgzYai
ZpHwam7QJGRETuIZujSMWk4ymGHuFAfiBitJxssbdk+EhDtBEscrEqXMPN135uq+
MzqabQbutGl8zG8VBg2ccsCCnyMRxp+wjCOq0xTfzOAt1rUeDLoOrQrmdYS1uLWf
6Wzv6AfTEQKBgQDNdtFq7LTjXZRYXsUX8wkSzGfBfc/exBLWsIFKSiUdJdZMM1eS
NfE2wLtZArU0bP5M2ON5zoE+XX8aSYnv/K+YTLn9s0WiolRxCf+pogvGDQVqgu5o
CbLGHpvrrWIm7wR/CsUrKmG/CTajCr6bsN6HtGoYiYVj88maQyT04e1EqwKBgCnb
m5iKOZZxHswNspKgwSO0xbZypwuq+zOAbME0FunfkyMziFOyp3quZs1lWaX/utkb
9Gi7K/eHjPC+znsouRWzHv1hOfGOwM1V44pZ7BvVk5vA29SyjhpAj/wB8npvsG7T
Cq/9INiZFJbL6CxpTSVNXw5UxDovGk6dFSAqMrtBAoGACPdcXbUsbP2NCyBm/mas
mGLXq7Hv9j42CiZac5BenJvtCG+5CsF+lENk+NHvVgZOg15VPWY+nBXOp/r5Miwy
aZHD4PHvhNGB24qMIYyAVPkLoKjt20qUqX85UtAEmWeB8xoSbz4BFzy2jRIdB5n2
96Un2UC4lh47F4jEe2TLp+o=
-----END PRIVATE KEY-----

				""";

		PrivateKey privateKey = loadPrivateKey(privateKeyPEM);
		PublicKey publicKey = extractPublicKey(privateKey); // Extract Public Key

		// Sign XML
		String signedXML = signXML(xmlData, privateKey, publicKey);

		// Output signed XML
		System.out.println(signedXML);
	}

	public static PrivateKey loadPrivateKey(String base64PrivateKey) throws Exception {
		String privateKeyPEM = base64PrivateKey.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", ""); // Remove newlines and spaces

		byte[] keyBytes = java.util.Base64.getDecoder().decode(privateKeyPEM);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(keySpec);
	}

	public static PublicKey extractPublicKey(PrivateKey privateKey) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		RSAPrivateCrtKey privCrtKey = (RSAPrivateCrtKey) privateKey;
		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privCrtKey.getModulus(), privCrtKey.getPublicExponent());
		return keyFactory.generatePublic(publicKeySpec);
	}

	public static String signXML(String xml, PrivateKey privateKey, PublicKey publicKey) throws Exception {
		// Parse XML
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

		// Create XML Signature Factory
		XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance("DOM");

		// Canonicalization and Signature Method
		CanonicalizationMethod canonicalizationMethod = sigFactory
				.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null);
		SignatureMethod signatureMethod = sigFactory.newSignatureMethod(SignatureMethod.RSA_SHA256, null);

		// Reference with SHA-256 Digest
		DigestMethod digestMethod = sigFactory.newDigestMethod(DigestMethod.SHA256, null);
		Transform transform = sigFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
		Reference reference = sigFactory.newReference("", digestMethod, Collections.singletonList(transform), null,
				null);

		// SignedInfo
		SignedInfo signedInfo = sigFactory.newSignedInfo(canonicalizationMethod, signatureMethod,
				Collections.singletonList(reference));

		// KeyInfo (Public Key)
		KeyInfoFactory kif = sigFactory.getKeyInfoFactory();
		KeyValue keyValue = kif.newKeyValue(publicKey);
		KeyInfo keyInfo = kif.newKeyInfo(Collections.singletonList(keyValue));

		// Sign the Document
		DOMSignContext domSignContext = new DOMSignContext(privateKey, doc.getDocumentElement());
		XMLSignature signature = sigFactory.newXMLSignature(signedInfo, keyInfo);
		signature.sign(domSignContext);

		// Convert signed XML to String
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));

		return writer.toString();
	}
}
