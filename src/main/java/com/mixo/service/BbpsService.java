package com.mixo.service;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mixo.dto.EBillDto;
import com.mixo.utils.AESEncryptor;
import com.mixo.utils.AlphaNumIdGenerator;

@Service
public class BbpsService {

	@Autowired
	AESEncryptor aesEncryptor;

	public ResponseEntity<?> category() {
		List<Map<String, String>> billersList = new ArrayList<>();

		try {
			URL url = new URL(
					"https://axis-bbps.transxt.in:8443/bbps/v3.0/billerservice/downloadMDM/billercategory/Electricity");
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("GET");

			httpConn.setRequestProperty("AUTH",
					"2e63b6f4882cf40bd9fa769c427766b3729977ffa403d8769611cb51e8b4a1e93a7a1b695797c0def1cdf4cdf2241b43d62e3406d0eb7c931fb0d6eee895f97a");

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonResponse = objectMapper.readTree(responseStream);

			System.out.println(jsonResponse);
			if (jsonResponse.has("biller")) {
				for (JsonNode billerNode : jsonResponse.get("biller")) {
					Map<String, String> billerData = new HashMap<>();
					billerData.put("billerId", billerNode.get("billerId").asText());
					billerData.put("billerName", billerNode.get("billerName").asText());
					billersList.add(billerData);
				}
			}

			return ResponseEntity.ok(billersList);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body(Collections.emptyList());
		}
	}

	public ResponseEntity<?> billerId(String billID) {
		try {
			URL url = new URL(
					"https://axis-bbps-uat.transxt.in/bbps-cou/v3.0/billerservice/downloadMDM/billerId/" + billID);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("GET");

			httpConn.setRequestProperty("AUTH", "lS8rCN9y1yJmqnguxK1z47dY4CbsllkRrgwTZM7wJYo=");

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonResponse = objectMapper.readTree(responseStream);

			List<Map<String, Object>> billersList = new ArrayList<>();

			if (jsonResponse.has("biller")) {
				for (JsonNode billerNode : jsonResponse.get("biller")) {
					Map<String, Object> billerData = new HashMap<>();
					billerData.put("billerId", billerNode.get("billerId").asText());
					billerData.put("billerName", billerNode.get("billerName").asText());

					// Extract customerParams if available
					List<Map<String, String>> customerParamsList = new ArrayList<>();
					if (billerNode.has("customerParams")) {
						for (JsonNode paramNode : billerNode.get("customerParams")) {
							Map<String, String> paramData = new HashMap<>();
							paramData.put("paramName", paramNode.get("paramName").asText());
							paramData.put("dataType", paramNode.get("dataType").asText());
							paramData.put("optional", paramNode.get("optional").asText());
							customerParamsList.add(paramData);
						}
					}

					billerData.put("customerParams", customerParamsList);
					billersList.add(billerData);
				}
			}

			return ResponseEntity.ok(billersList);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body(Collections.emptyList());
		}
	}

	public ResponseEntity<?> billId(String billID) {
		// TODO Auto-generated method stub

		String url = "{\"chId\":1,\"custDetails\":{\"mobileNo\":\"7021398101\",\"customerTags\":[{\"name\":\"EMAIL\",\"value\":\"mk.chekuri@gmail.com\"}]},\"agentDetails\":{\"agentId\":\"AM01YKS077INTU000001\",\"deviceTags\":[{\"name\":\"INITIATING_CHANNEL\",\"value\":\"MOBB\"},{\"name\":\"IMEI\",\"value\":\"448674528976410\"},{\"name\":\"OS\",\"value\":\"android\"},{\"name\":\"APP\",\"value\":\"NPCIAPP\"},{\"name\":\"IP\",\"value\":\"124.170.23.28\"}]},\"billDetails\":{\"billerId\":\"MAHA00000MAH01\",\"customerParams\":[{\"name\":\"Consumer No\",\"value\":\"9865778954\"},{\"name\":\"BU\",\"value\":\"8596\"}]}}";
		String secret = "10f2b2d5d58eed81af9426e0d49938f1e110abf6242838987b9e064e1c6cf84f4e25b781993112dab30bae56defc0470f03b1e3be777994b2a8ac1a91c5b6d0c";

		String aes = aesEncryptor.encrypt(url, secret);
		return ResponseEntity.ok(aes);
	}

	public String fatchBill(EBillDto dto) {

		String jsonPayload = generateJsonRequest(dto);
//		jsonPayload = jsonPayload.replaceAll("\"", "\\\"");
//		System.out.println(jsonPayload);
		String secret = "10f2b2d5d58eed81af9426e0d49938f1e110abf6242838987b9e064e1c6cf84f4e25b781993112dab30bae56defc0470f03b1e3be777994b2a8ac1a91c5b6d0c";

		String aes = aesEncryptor.encrypt(jsonPayload, secret);
		try {

			JSONObject request = new JSONObject();
			request.put("request", aes);
			URL url = new URL("https://axis-bbps-uat.transxt.in/bbps-cou/v3.0/npciservice/fetch");
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("POST");

			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("AUTH", "FAlvwU/5e/TXXbPCPmBNoV9U6pKVtUDZZGKyTBcHoho=");

			httpConn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write(request.toString());
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			String response = s.hasNext() ? s.next() : "";
			System.out.println(response);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public String generateJsonRequest(EBillDto dto) {
		try {
			JSONObject json = new JSONObject();
			json.put("chId", dto.getChId());

			JSONObject custDetails = new JSONObject();
			custDetails.put("mobileNo", dto.getMobileNo());
			custDetails.put("customerTags", List.of(Map.of("name", "EMAIL", "value", dto.getEmail())));

			JSONObject agentDetails = new JSONObject();
			agentDetails.put("agentId", "AM01YKS077INTU000001");
			agentDetails.put("deviceTags", List.of(Map.of("name", "INITIATING_CHANNEL", "value", dto.getChannel()),
					Map.of("name", "IMEI", "value", dto.getImei()), Map.of("name", "OS", "value", dto.getOs()),
					Map.of("name", "APP", "value", dto.getAppName()), Map.of("name", "IP", "value", dto.getIp())));

			JSONObject billDetails = new JSONObject();
			billDetails.put("billerId", dto.getBillerId());
			billDetails.put("customerParams", dto.getCustomerParams());

			json.put("custDetails", custDetails);
			json.put("agentDetails", agentDetails);
			json.put("billDetails", billDetails);

			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String fatchBill2(EBillDto dto) {
		String jsonPayload = generateJsonRequest2(dto);
//		jsonPayload = jsonPayload.replaceAll("\"", "\\\"");
//		System.out.println(jsonPayload);
		String secret = "10f2b2d5d58eed81af9426e0d49938f1e110abf6242838987b9e064e1c6cf84f4e25b781993112dab30bae56defc0470f03b1e3be777994b2a8ac1a91c5b6d0c";

		String aes = aesEncryptor.encrypt(jsonPayload, secret);
		try {

			JSONObject request = new JSONObject();
			request.put("request", aes);
			URL url = new URL("https://axis-bbps-uat.transxt.in/bbps-cou/v3.0/npciservice/pay");
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("POST");

			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("AUTH", "FAlvwU/5e/TXXbPCPmBNoV9U6pKVtUDZZGKyTBcHoho=");

			httpConn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write(request.toString());
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
					: httpConn.getErrorStream();
			try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
				String response = s.hasNext() ? s.next() : "";
				System.out.println(response);
				return response;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public String generateJsonRequest2(EBillDto dto) {
		try {
			JSONObject json = new JSONObject();
			json.put("chId", dto.getChId());
			json.put("refId", AlphaNumIdGenerator.generateId(18));
			json.put("clientRequestId", AlphaNumIdGenerator.generateId(17));

			// Customer Details
			JSONObject custDetails = new JSONObject();
			custDetails.put("mobileNo", dto.getMobileNo());
			custDetails.put("customerTags", List.of(Map.of("name", "EMAIL", "value", dto.getEmail())));
			json.put("custDetails", custDetails);

			// Agent Details
			JSONObject agentDetails = new JSONObject();
			agentDetails.put("agentId", "AM01YKS077INTU000001");
			agentDetails.put("deviceTags", List.of(Map.of("name", "INITIATING_CHANNEL", "value", dto.getChannel()),
					Map.of("name", "IMEI", "value", dto.getImei()), Map.of("name", "OS", "value", dto.getOs()),
					Map.of("name", "APP", "value", dto.getAppName()), Map.of("name", "IP", "value", dto.getIp())));
			json.put("agentDetails", agentDetails);

			// Amount Details
			JSONObject amountDetails = new JSONObject();
			amountDetails.put("amount", "100");
			amountDetails.put("currency", "356");
			amountDetails.put("custConvFee", "0");
			amountDetails.put("couCustConvFee", "10");
			json.put("amountDetails", amountDetails);

			// Bill Details
			JSONObject billDetails = new JSONObject();
			billDetails.put("billerId", dto.getBillerId());
			billDetails.put("customerParams", dto.getCustomerParams()); // should be a List<Map<String, String>>
			json.put("billDetails", billDetails);

			// Payment Details
			JSONObject paymentDetails = new JSONObject();
			paymentDetails.put("offusPay", "No");
			paymentDetails.put("quickPay", "No");
			paymentDetails.put("splitPay", "No");
			paymentDetails.put("paymentMode", "Credit Card");
			paymentDetails.put("paymentInfo",
					List.of(Map.of("name", "CardNum|AuthCode", "value", "4336620020624963|123456")));
			json.put("paymentDetails", paymentDetails);

			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
