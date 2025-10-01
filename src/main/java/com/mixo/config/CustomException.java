package com.mixo.config;

public class CustomException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5053251004211402460L;
	private final String code;
	private final String message;

	public CustomException(String message, String code) {
		this.message = message;
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
