package com.mixo.utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class Key {

	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException,
			IOException, GeneralSecurityException {

		String anc = """
				{
				  "orgId": "200391",
				  "txnId": "HML9X7G2W5A1Y3Z6B8C4D0EJQKRMNVTPT6L",
				  "consentFlag": "Yes",
				  "mode": "SELF",
				  "callBackIP": "localhost:8090/api/loans/npc-response"
				}


								""";

		System.out.println(anc.replaceAll("\\s", ""));
		SignatureGenerator signatureGenerator = new SignatureGenerator();
		String asbsd = signatureGenerator.signatureGenerate(anc.replaceAll("\\s", ""));

		System.out.println(asbsd);
//		URL url = new URL(
//				"https://globalcertzone.npci.org.in/aeps/ReqKycData/2.0/urn:txnid:HML12345675565273665474887");
//
//		System.out.println(url);
//		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//		httpConn.setRequestMethod("POST");
//
//		InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream()
//				: httpConn.getErrorStream();
//		Scanner s = new Scanner(responseStream).useDelimiter("\\A");
//		String response = s.hasNext() ? s.next() : "";
//		System.out.println(response);

//		System.out.println(asbsd);
		// TODO Auto-generated method stub

	}

}
