package com.mixo.service;

import org.springframework.web.multipart.MultipartFile;

public interface AwsS3Service {

	public String uploadFile(MultipartFile file, String keyPrefix);

	public String uploadJsonToS3(String objectKey, String jsonContent);

}
