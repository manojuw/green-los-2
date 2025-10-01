package com.mixo.test;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONObject;

import com.mixo.utils.AESEncryptor;

public class SettlementTest {
	public static void main(String[] args) {

		fatchBill();

	}

	public static String fatchBill() {

		String jsonPayload = generateJsonRequest();
		System.out.println(jsonPayload);
//		jsonPayload = jsonPayload.replaceAll("\"", "\\\"");
//		System.out.println(jsonPayload);
		String secret = "10f2b2d5d58eed81af9426e0d49938f1e110abf6242838987b9e064e1c6cf84f4e25b781993112dab30bae56defc0470f03b1e3be777994b2a8ac1a91c5b6d0c";
		AESEncryptor aesEncryptor = new AESEncryptor();
		String aes = aesEncryptor.encrypt(jsonPayload, secret);
		try {

			JSONObject request = new JSONObject();
			request.put("request", aes);
			URL url = new URL("https://axis-bbps-uat.transxt.in/bbps-cou/v3.0/npciservice/billValidation");
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

	public static String generateJsonRequest() {
		try {
			JSONObject json = new JSONObject();
			String anc = """
										{
					    "chId": 1,
					    "agentDetails": {
					        "agentId": "AM01YKS077INTU000001"
					    },
					    "billDetails": {
					        "billerId": "CERT12000NAT01",
					        "customerParams": [
					            {
					                "name": "Mobile Number",
					                "value": "8798765607"
					            },
					            {
					                "name": "Circle",
					                "value": "Mumbai"
					            }
					        ]
					    }
					}
										""";

			return anc.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static List<OrgInwardRemitEntity> generateRandomRemits(int count, int minAmt, int maxAmt) {
		List<OrgInwardRemitEntity> list = new ArrayList<>();
		Random rand = new Random();
		for (int i = 0; i < count; i++) {
			double amt = minAmt + rand.nextDouble(maxAmt - minAmt + 1);
			list.add(new OrgInwardRemitEntity(amt));
		}
		return list;
	}

	private static void processChunk(List<OrgInwardRemitEntity> chunk, double totalAmount, int index) {
		String txnId = "TXN-" + index;

		System.out.println("Processing txnId: " + txnId + " | Total: " + totalAmount + " | Items: " + chunk.size());

		for (OrgInwardRemitEntity remit : chunk) {
			remit.setSettlementTxnId(txnId);
			remit.setSettlementStatus("PROCESSING");
		}
	}
}
