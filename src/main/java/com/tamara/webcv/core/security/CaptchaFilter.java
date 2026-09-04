package com.tamara.webcv.core.security;

import com.tamara.webcv.core.ClientIpResolver;
import com.tamara.webcv.core.Constants;
import com.tamara.webcv.core.dto.ErrorResponse;
import com.tamara.webcv.core.service.TurnstileService;
import com.tamara.webcv.exception.TokenValidationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Service
@Slf4j
public class CaptchaFilter extends OncePerRequestFilter {

    @Autowired
    private ClientIpResolver resolver;

    @Autowired
    private TurnstileService service;

    @Autowired
    private ObjectMapper mapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !(request.getMethod().equals("POST") && request.getRequestURI().equals(Constants.MESSAGE_FULL_ENDPOINT));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(Constants.TURNSTILE_HEADER_NAME);
        String clientIp = resolver.getClientIp(request);

        try {
            service.verify(token, clientIp);
            filterChain.doFilter(request, response);
        } catch (TokenValidationException e) {
            log.warn(e.getMessage(), e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ErrorResponse errorResponse = new ErrorResponse(
                    "TOKEN_VERIFICATION_FAILED",
                    "Can't handle your request. Please retry later."
            );

            mapper.writeValue(response.getWriter(), errorResponse);
        }

    }
}

