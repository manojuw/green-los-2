package com.mixo.config;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mixo.service.ApiKeyService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

	private final ApiKeyService apiKeyService;

	public ApiKeyFilter(ApiKeyService apiKeyService) {
		this.apiKeyService = apiKeyService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getRequestURI();
		if (path.startsWith("/api/lending/")) {
			String apiKey = request.getHeader("X-API-KEY");
			if (apiKey == null || !apiKey.equals(apiKeyService.getApiKey())) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("Invalid or missing API key");
				return;
			}
		}

		filterChain.doFilter(request, response);
	}
}
