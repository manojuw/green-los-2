package com.mixo.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class CorrelationIdFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_KEY = "correlationId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Retrieve correlation ID from request header, or generate one
        String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        // Add correlation ID to MDC (Mapped Diagnostic Context)
        MDC.put(CORRELATION_ID_KEY, correlationId);

        try {
            // Continue processing the request
            chain.doFilter(request, response);
        } finally {
            // Remove the correlation ID from MDC after request processing
            MDC.remove(CORRELATION_ID_KEY);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No init required
    }

    @Override
    public void destroy() {
        // No destroy required
    }
}
