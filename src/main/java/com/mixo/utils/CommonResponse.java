package com.mixo.utils;

public class CommonResponse {
	
	
	private int httpCode;
	private String code;
	private String message;
	private Object data;

	public int getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public CommonResponse(int httpCode, String code, String message, Object data) {
		super();
		this.httpCode = httpCode;
		this.code = code;
		this.message = message;
		this.data = data;
	}

}
