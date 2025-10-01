package com.mixo.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mixo.utils.CommonResponse;
import com.mixo.utils.ResponseCodeEnum;

@ControllerAdvice
public class SecurityControllerAdvice {

	@ModelAttribute
	public void addAuthenticationToModel(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

			// Extract role names and pass them to the view
			Collection<String> roles = authorities.stream().map(GrantedAuthority::getAuthority)
					.collect(Collectors.toList());
			model.addAttribute("userName", authentication.getName());
			model.addAttribute("authentication", roles);
		}

	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<CommonResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}
		CommonResponse response = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
				ResponseCodeEnum.BAD_REQUEST.getCode(), ResponseCodeEnum.BAD_REQUEST.getMessage(), errors);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<CommonResponse> handleMissingHeader(MissingRequestHeaderException ex) {
		CommonResponse response = new CommonResponse(HttpStatus.BAD_REQUEST.value(),
				ResponseCodeEnum.BAD_REQUEST.getCode(), ResponseCodeEnum.BAD_REQUEST.getMessage(),
				"Missing required header: " + ex.getHeaderName());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}
