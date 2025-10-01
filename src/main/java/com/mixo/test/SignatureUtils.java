package com.mixo.test;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Base64;

public class SignatureUtils {

	public static String signData(String data, PrivateKey privateKey) throws Exception {
		java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
		signature.initSign(privateKey);
		signature.update(data.getBytes());
		return Base64.getEncoder().encodeToString(signature.sign());
	}

	public static PrivateKey loadPrivateKey(String keyStorePath, String keyStorePassword, String alias)
			throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(SignatureUtils.class.getClassLoader().getResourceAsStream(keyStorePath),
				keyStorePassword.toCharArray());
		return (PrivateKey) keyStore.getKey(alias, keyStorePassword.toCharArray());
	}
}
