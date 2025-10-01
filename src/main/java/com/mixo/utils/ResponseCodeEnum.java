package com.mixo.utils;

public enum ResponseCodeEnum {
	
	
	SUCCESS							("HM000", "LOAN SUCCESSFUL"),
    FAILURE							("HM001", "LOAN FAILED"),
    INSUFFICIENT_FUNDS				("HM002", "Insufficient funds"),
    INVALID_REQUEST					("HM003", "Invalid request"),
    
    USER_NOT_FOUND 					("HM010", "User not Found"),
    INACTVE_USER		 			("HM011", "User is inactive for processing."),
    DUPLICATE_REQUEST 				("HM012", "Duplicate Order id found."),
    ERROR_IN_PAYOUT					("HM013", "Error occured in Payout"),
    NO_RESULT						("HM014", "No result found"),
    REQUEST_PROCESSED				("HM015", "Request Processed"),
    NO_MAPPING_FOUND				("HM016", "Mapping is missing in configruation."),
    INTERNAL_FAILURE				("HM017", "Internal System Failure"),
    MISSING_MANDATORY_PARAMETERS	("HM018", "Missing mandatory parameters"),
    AUTH_ERROR						("HM019", "Auth error. No amount has been debited"),
    INVALID_PRODUCT_ID           	("HM020", "Invalid Product Id"),
    
   
    LOAN_REQUEST_FAILED           	("HM021", "Loan Request Failed"),
    
    FILE_UPLOAD_FAILED				("HM022", "File Upload Failed"),
    INVALID_LENDER_ID				("HM023", "Invalid Lender Id"),
    INVALID_LOAN_AMOUNT				("HM024", "Invalid Loan Amount"),
    INVALID_EMI_TIME				("HM025", "Invalid EMI Time"),
    PARTNERSHIP_EXPIRED				("HM026", "Partnership Expired"),
    INVALID_RATE_OF_INTEREST				("HM027", "Invalid Rate Of Interest"),
    EXPIRED_USER_UNIQUE_ID				("HM028", "Expired User Unique Id"),
    INVALID_VERSION			           	("HM029", "Invalid API Version"),
	BAD_REQUEST						("HM400",  "Bad Request"), 
	INSUFFICIENT_BALANCE            ("HM401",  "Insufficient Balance"),
	REQUEST_INITIALIED_SUCCESSFULLY       ("HM000",  "Request Processed Successfully"),
	MAX_PAYOUT_LIMIT           				("HM402",  "Amount is greater than max payout limit"),
	IP_ADDRESS_NOT_WHITE_LISTED            ("HM403",  "IP address not whitelisted"),
	INVALID_SECRET            				("HM404",  "Invalid Secret"),
	
	INVALID_BILLER_CODE            				("HM405",  "Invalid BillerCodes"),  
	
	
	
	;

	private final String code;
	private final String message;

	ResponseCodeEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
