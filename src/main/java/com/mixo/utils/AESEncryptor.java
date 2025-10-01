package com.mixo.utils;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;


@Component
public final class AESEncryptor {
	
	private static final String UTF8 = "UTF-8";
	private String AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding";

	/**
	 * Encrypt data with key.
	 */
	public String encrypt(String message, String sercretKey) {
		String base64EncryptedString = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digestOfPassword = md.digest(sercretKey.getBytes(UTF8));
			byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
			byte[] iv = Arrays.copyOf(digestOfPassword, 16);
			SecretKey key = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
			byte[] plainTextBytes = message.getBytes(UTF8);
			byte[] buf = cipher.doFinal(plainTextBytes);
			byte[] base64Bytes = Base64.encodeBase64(buf);
			base64EncryptedString = new String(base64Bytes);
		} catch (Exception ex) {
			
		}
		return base64EncryptedString;
	}

	/**
	 * Decrypt data with key.
	 */
	public String decrypt(String encryptedText, String sercretKey) {
		String base64DecryptedString = "";
		byte[] plainText = null;
		try {
			byte[] message = Base64.decodeBase64(encryptedText.getBytes(UTF8));
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digestOfPassword = md.digest(sercretKey.getBytes(UTF8));
			byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
			byte[] iv = Arrays.copyOf(digestOfPassword, 16);
			SecretKey key = new SecretKeySpec(keyBytes, "AES");
			Cipher decipher = Cipher.getInstance(AES_CBC_PKCS5);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			decipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

			plainText = decipher.doFinal(message);
			base64DecryptedString = new String(plainText, UTF8);

		} catch (Exception ex) {
			
		}
		return base64DecryptedString;
	}
}

