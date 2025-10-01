package com.mixo.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	/**
	 * Handle validation errors (e.g., @Valid/@Validated failures).
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		log.error("Validation failed.", ex);
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle generic exceptions.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleGeneralExceptions(Exception ex) {
		log.error("An unexpected error occurred. Please try again later.", ex);
		Map<String, String> response = new HashMap<>();
		response.put("error", "E500");
		response.put("details", "An unexpected error occurred. Please try again later.");

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<Map<String, String>> handleCustomExceptions(Exception ex) {
		log.error("An unexpected error occurred. Please try again later.", ex);
		Map<String, String> response = new HashMap<>();
		response.put("error", ex.getMessage());
		response.put("details", "An unexpected error occurred. Please try again later.");

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handle custom application exceptions.
	 */
}
