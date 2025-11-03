package com.mixo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;

@Configuration
public class KmsConfig {

	@Bean
	 KmsClient kmsClient() {
		return KmsClient.builder().region(Region.AP_SOUTH_1) // Replace with your region
				.credentialsProvider(StaticCredentialsProvider.create(
						AwsBasicCredentials.create("<>", "<>")))
				.build();
	}

}
