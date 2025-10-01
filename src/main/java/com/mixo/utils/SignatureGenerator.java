package com.mixo.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class SignatureGenerator {
	public static final String CERTFILE = "C:\\MSI\\mufinpay_certificates\\mufinpay_certificates\\mufinpay250.pem";

	public String signatureGenerate(String incomingPayload) throws NoSuchAlgorithmException, InvalidKeyException,
			SignatureException, IOException, GeneralSecurityException {
		String signatureValue = null;
		try {
			PrivateKey privateKey = loadPrivateKey(CERTFILE);
			Signature sign = Signature.getInstance("SHA256withRSA");
			sign.initSign(privateKey);
			sign.update(incomingPayload.getBytes(StandardCharsets.UTF_8));
			byte[] signature = sign.sign();
			signatureValue = Base64.getEncoder().encodeToString(signature);
			System.out.println("Signature: " + signatureValue);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return signatureValue;
	}

	private PrivateKey loadPrivateKey(String pemFilePath) throws IOException, GeneralSecurityException {
		String key = new String(Files.readAllBytes(Paths.get(pemFilePath)), StandardCharsets.UTF_8);
		key = key.replaceAll("-----BEGIN.*?-----", "").replaceAll("-----END.*?-----", "").replaceAll("\\s", "");
		byte[] decodedKey = Base64.getDecoder().decode(key);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(keySpec);
	}

}
