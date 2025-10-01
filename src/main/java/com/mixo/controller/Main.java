package com.mixo.controller;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		String url = "{\"billDetails\":{\"billerId\":\"MAHA00000MAH01\",\"customerParams\":[{\"name\":\"Consumer No\",\"value\":\"9865778954\"},{\"name\":\"BU\",\"value\":\"8596\"}]},\"agentDetails\":{\"deviceTags\":[{\"name\":\"INITIATING_CHANNEL\",\"value\":\"MOBB\"},{\"name\":\"IMEI\",\"value\":\"448674528976410\"},{\"name\":\"OS\",\"value\":\"android\"},{\"name\":\"APP\",\"value\":\"NPCIAPP\"},{\"name\":\"IP\",\"value\":\"124.170.23.28\"}],\"agentId\":\"AM01YKS077INTU000001\"},\"custDetails\":{\"mobileNo\":\"9004312101\",\"customerTags\":[{\"name\":\"EMAIL\",\"value\":\"mk.chekuri@gmail.com\"}]},\"chId\":1}\r\n"
				+ "";
		
		url.replace("\"", "\\\"");
		
		System.out.println(url);
	}
	
	public static String[] extractNames(String fullName) {
        String[] names = fullName.trim().split("\\s+"); 

        if (names.length == 1) { // Single name
            return new String[]{names[0], "", ""}; 
        } else if (names.length == 2) { // Two-part name
            return new String[]{names[0], "", names[1]}; 
        } else { // Multiple names
            String firstName = names[0];
            String middleName = "";
            String lastName = names[names.length - 1];

            // Concatenate middle names if multiple exist
            for (int i = 1; i < names.length - 1; i++) {
                middleName += names[i] + " ";
            }
            middleName = middleName.trim();

            return new String[]{firstName, middleName, lastName};
        }
    }
}
