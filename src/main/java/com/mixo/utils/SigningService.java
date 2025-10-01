package com.mixo.utils;

import org.springframework.stereotype.Service;

@Service
public class SigningService {

//	private final KmsClient kmsClient;
//	private String keyId = "arn:aws:kms:ap-south-1:975049987477:key/5251a8e8-1389-40e9-bddc-df4e282dc452";
//
//	public SigningService(KmsClient awsKms) {
//		this.kmsClient = awsKms;
//	}
//
//	public byte[] signMessage(String message) throws UnsupportedEncodingException, NoSuchAlgorithmException {
//		SdkBytes messageSdkBytes = SdkBytes.fromByteArray(message.getBytes("UTF-8"));
//
//		SignRequest signRequest = SignRequest.builder().keyId(keyId).messageType(MessageType.RAW)
//				.message(messageSdkBytes).signingAlgorithm(SigningAlgorithmSpec.ECDSA_SHA_256).build();
//		SignResponse signResponse = kmsClient.sign(signRequest);
//
//		return signResponse.signature().asByteArray();
//	}
//
//	public Boolean isValidSignature(String message, byte[] signature)
//			throws UnsupportedEncodingException, NoSuchAlgorithmException {
//
//		SdkBytes messageSdkBytes = SdkBytes.fromByteArray(message.getBytes("UTF-8"));
//		SdkBytes signatureSdkBytes = SdkBytes.fromByteArray(signature);
//
//		VerifyRequest verifyRequest = VerifyRequest.builder().keyId(keyId).messageType(MessageType.RAW)
//				.message(messageSdkBytes).signingAlgorithm(SigningAlgorithmSpec.ECDSA_SHA_256)
//				.signature(signatureSdkBytes).build();
//		VerifyResponse verifyResponse = kmsClient.verify(verifyRequest);
//
//		return verifyResponse.signatureValid();
//	}

}
