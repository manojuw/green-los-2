package com.mixo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {

	@Bean
	S3Client s3Client() {
		return S3Client.builder().region(Region.AP_SOUTH_1) // Replace with your region
				.credentialsProvider(StaticCredentialsProvider.create(
//						AwsBasicCredentials.create("AKIA45Y2R6GVYHXDBJQF", "6Ai2VEHpabuQ/eMd6sA/rVNWRUhBnaTl2IWSOGTc")))
				AwsBasicCredentials.create("AKIA45Y2R6GVQGMODEEB", "wv5bzVFmC9ne61oFQC9MiYXq9hV5GUxP7XUZzTjl")))
				.build();
	}

}
