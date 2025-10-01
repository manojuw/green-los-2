package com.mixo.utils;

import java.time.LocalDate;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class ValidatedRequest {

	private static final String RESPONSE = "response";
	private static final String IN_PROFILE_RESPONSE = "INProfileResponse";
	private static final String CAIS_ACCOUNT = "CAIS_Account";
	private static final String CAIS_ACCOUNT_DETAILS = "CAIS_Account_DETAILS";
	private static final String CAIS_ACCOUNT_HISTORY_ARR = "CAIS_Account_History";

	public static JSONObject validateCreditScore(String creditScore) {

		JSONObject cibilResponse = new JSONObject();

		JSONObject jsonResponse = new JSONObject(creditScore);

		if (!jsonResponse.optString("message").isEmpty()
				&& jsonResponse.optString("message").equalsIgnoreCase("0000")) {
			cibilResponse.put("RESPONSE_CODE", "L000");
			cibilResponse.put("RESPONSE_MESSAGE", "SUCCESS");
			JSONObject cibilData = jsonResponse.optJSONObject("RESPONSE".toLowerCase());
			JSONObject INProfileResponse = cibilData.optJSONObject("INProfileResponse");
			JSONObject SCORE = INProfileResponse.optJSONObject("SCORE");

			cibilResponse.put("SCORE", SCORE.optString("BureauScore"));
			JSONObject CAIS_Account = INProfileResponse.optJSONObject("CAIS_Account");
			JSONObject CAIS_Summary = CAIS_Account.optJSONObject("CAIS_Summary");
			JSONObject Credit_Account = CAIS_Summary.optJSONObject("Credit_Account");
			cibilResponse.put("CreditAccountActive", Credit_Account.optString("CreditAccountActive"));
			cibilResponse.put("CreditAccountDefault", Credit_Account.optString("CreditAccountDefault"));
			cibilResponse.put("CreditAccountClosed", Credit_Account.optString("CreditAccountClosed"));
			cibilResponse.put("CADSuitFiledCurrentBalance", Credit_Account.optString("CADSuitFiledCurrentBalance"));

			LocalDate currentDate = LocalDate.now();
			int currentYear = currentDate.getYear();
			Set<String> set = Set.of(String.valueOf(currentYear), String.valueOf(currentYear - 1),
					String.valueOf(currentYear - 2));

			StringBuilder firstYearBuilder = new StringBuilder();
			StringBuilder secondYearBuilder = new StringBuilder();
			StringBuilder acctDetailsBuilder = new StringBuilder();
			if (GeneralUtility.isJsonValid(creditScore)) {
				JSONObject caisJSONObject = new JSONObject(creditScore);
				if (GeneralUtility.validateJsonPayloadParameter(caisJSONObject, RESPONSE)) {
					JSONObject responseJSONObject = caisJSONObject.getJSONObject(RESPONSE);
					if (GeneralUtility.validateJsonPayloadParameter(responseJSONObject, IN_PROFILE_RESPONSE)) {
						JSONObject inProgressJSONObject = responseJSONObject.getJSONObject(IN_PROFILE_RESPONSE);
						if (GeneralUtility.validateJsonPayloadParameter(inProgressJSONObject, CAIS_ACCOUNT)) {
							JSONObject caisAcctJSONObject = inProgressJSONObject.getJSONObject(CAIS_ACCOUNT);
							if (GeneralUtility.validateJsonArrayPayloadParameter(caisAcctJSONObject,
									CAIS_ACCOUNT_DETAILS)) {
								JSONArray caisAccountDetailsArr = caisAcctJSONObject.getJSONArray(CAIS_ACCOUNT_DETAILS);

								for (int i = 0; i < caisAccountDetailsArr.length(); i++) {

									JSONObject caisAccountDetail = caisAccountDetailsArr.getJSONObject(i);
									String accountStatus = caisAccountDetail.optString("Account_Status");
									String dateReported = caisAccountDetail.optString("Date_Reported");
									acctDetailsBuilder.append(accountStatus + "-" + dateReported).append(",");
									cibilResponse.put("ACCOUNT_DETAILS", acctDetailsBuilder.toString());

									if (GeneralUtility.validateJsonArrayPayloadParameter(caisAccountDetail,
											CAIS_ACCOUNT_HISTORY_ARR)) {
										JSONArray caisAccountHistoryArr = caisAccountDetail
												.getJSONArray(CAIS_ACCOUNT_HISTORY_ARR);
										for (int j = 0; j < caisAccountHistoryArr.length(); j++) {
											JSONObject daysPastDueObj = caisAccountHistoryArr.getJSONObject(j);
											// start for JSONArray
											String year = daysPastDueObj.optString("Year");
											String month = daysPastDueObj.optString("Month");
											if (set.contains(year)) {
												String daysPastDue = daysPastDueObj.optString("Days_Past_Due");
												if (StringUtils.isNotBlank(daysPastDue)
														&& NumberUtils.isCreatable(daysPastDue)) {

													boolean isSecondYearValid = DateTimeCreator.isValidDateDiffernce(
															year + month
																	+ DateTimeCreator.getLastDayOfMonth(year, month),
															2);
													if (!isSecondYearValid)
														continue;
													secondYearBuilder.append(daysPastDue).append("|");
													// below checks isFirstYearValid
													if (DateTimeCreator.isValidDateDiffernce(
															year + month
																	+ DateTimeCreator.getLastDayOfMonth(year, month),
															1))
														firstYearBuilder.append(daysPastDue).append("|");
												}
											} // end for JSONArray
										}
									}
								}
							} else if (GeneralUtility.validateJsonPayloadParameter(caisAcctJSONObject,
									CAIS_ACCOUNT_DETAILS)) {
								JSONObject caisAccountDetailsObject = caisAcctJSONObject
										.getJSONObject(CAIS_ACCOUNT_DETAILS);

								String accountStatus = caisAccountDetailsObject.optString("Account_Status");
								String dateReported = caisAccountDetailsObject.optString("Date_Reported");
								cibilResponse.put("ACCOUNT_DETAILS",
										new StringBuilder().append(accountStatus + "-" + dateReported).toString());

								if (GeneralUtility.validateJsonArrayPayloadParameter(caisAccountDetailsObject,
										CAIS_ACCOUNT_HISTORY_ARR)) {
									JSONArray caisAccountHistoryArr = caisAccountDetailsObject
											.getJSONArray(CAIS_ACCOUNT_HISTORY_ARR);
									for (int j = 0; j < caisAccountHistoryArr.length(); j++) {
										JSONObject daysPastDueObj = caisAccountHistoryArr.getJSONObject(j);

										// start for JSONObject
										String year = daysPastDueObj.optString("Year");
										String month = daysPastDueObj.optString("Month");
										if (set.contains(year)) {
											String daysPastDue = daysPastDueObj.optString("Days_Past_Due");
											if (StringUtils.isNotBlank(daysPastDue)
													&& NumberUtils.isCreatable(daysPastDue)) {
												boolean isSecondYearValid = DateTimeCreator.isValidDateDiffernce(
														year + month + DateTimeCreator.getLastDayOfMonth(year, month),
														2);
												if (!isSecondYearValid)
													continue;
												secondYearBuilder.append(daysPastDue).append("|");
												// below checks isFirstYearValid
												if (DateTimeCreator.isValidDateDiffernce(
														year + month + DateTimeCreator.getLastDayOfMonth(year, month),
														1))
													firstYearBuilder.append(daysPastDue).append("|");
											}
										} // end for JSONObject
									}
								}
							}
						}
					}
				}
				cibilResponse.put("FIRST_YEAR", firstYearBuilder.toString());
				cibilResponse.put("SECOND_YEAR", secondYearBuilder.toString());
			}
			return cibilResponse;
		} else if (!jsonResponse.optString("message").isEmpty()
				&& jsonResponse.optString("message").contains("SYS100004")) {
			cibilResponse.put("RESPONSE_CODE", "L000");
			cibilResponse.put("RESPONSE_MESSAGE", "SUCCESS");
			cibilResponse.put("NTC", "NTC");
			return jsonResponse;
		} else {
			jsonResponse.put("RESPONSE_CODE", "L999");
			return jsonResponse;
		}
	}

}
