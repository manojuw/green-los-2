package com.mixo.serviceimpl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mixo.service.AwsS3Service;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@Slf4j
public class AwsS3ServiceImpl implements AwsS3Service {

	@Autowired
	private S3Client s3Client;

//	@Value("${aws.s3.bucket}")
//	private String bucketName;

	public String uploadFile(MultipartFile file, String keyPrefix) {

		String fileName = keyPrefix + "/" + file.getOriginalFilename();
		try {
			try {
				s3Client.putObject(PutObjectRequest.builder().bucket("losdocx").key(fileName).acl("public-read") // Optional:
																												// Makes
																												// the
																												// file
																												// publicly
																												// readable
						.build(), RequestBody.fromBytes(file.getBytes()));
			} catch (AwsServiceException | SdkClientException | IOException e) {
				throw new RuntimeException("Error uploading file to S3: " + e.getMessage(), e);// TODO Auto-generated
																								// catch block
			}

			// Construct and return the file URL
			return s3Client.utilities().getUrl(builder -> builder.bucket("losdocx").key(fileName)).toString();

		} catch (S3Exception e) {
			throw new RuntimeException("Error uploading file to S3: " + e.getMessage(), e);
		}
	}

	// Upload the JSON content to S3
	public String uploadJsonToS3(String objectKey, String jsonContent) {
		String bucketName = "losdocx";
		// Upload the JSON content to S3
		PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(objectKey).build();

		PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromString(jsonContent));

		// Construct the S3 URL
		String s3Url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, "ap-south-1", objectKey);

		return s3Url;
	}
}
