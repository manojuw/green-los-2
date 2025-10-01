package com.mixo.serviceimpl;

import java.util.Base64;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;

@Service
public class AwsKmsService {

    private final KmsClient kmsClient;

    public AwsKmsService(KmsClient kmsClient) {
        this.kmsClient = kmsClient;
    }

    public String encrypt(String keyId, String plaintext) {
        SdkBytes dataToEncrypt = SdkBytes.fromUtf8String(plaintext);
        EncryptRequest encryptRequest = EncryptRequest.builder()
                .keyId(keyId)
                .plaintext(dataToEncrypt)
                .build();

        EncryptResponse encryptResponse = kmsClient.encrypt(encryptRequest);
        byte[] encryptedBytes = encryptResponse.ciphertextBlob().asByteArray();
        
        // Convert the encrypted bytes to a Base64-encoded string
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedDataBase64) {
        // Decode the Base64 string back to byte[]
        byte[] encryptedData = Base64.getDecoder().decode(encryptedDataBase64);
        
        SdkBytes encryptedBytes = SdkBytes.fromByteArray(encryptedData);
        DecryptRequest decryptRequest = DecryptRequest.builder()
                .ciphertextBlob(encryptedBytes)
                .build();

        DecryptResponse decryptResponse = kmsClient.decrypt(decryptRequest);
        return decryptResponse.plaintext().asUtf8String();
    }
}

