package com.tamara.webcv.core.security;

import com.tamara.webcv.core.ClientIpResolver;
import com.tamara.webcv.core.Constants;
import com.tamara.webcv.core.dto.ErrorResponse;
import com.tamara.webcv.core.service.RateLimiterService;
import com.tamara.webcv.exception.RateLimitExceededException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimiterService service;

    @Autowired
    private ClientIpResolver ipResolver;

    @Autowired
    private ObjectMapper mapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !(request.getMethod().equals("POST") && request.getRequestURI().equals(Constants.MESSAGE_FULL_ENDPOINT));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientIp = ipResolver.getClientIp(request);

        try {
            service.checkLimit(clientIp);
            filterChain.doFilter(request, response);
        } catch (RateLimitExceededException exception) {
            log.warn(exception.getMessage(), exception);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ErrorResponse errorResponse = new ErrorResponse(
                    "MESSAGE_LIMIT_EXCEEDED",
                    "Can't handle your request. Please retry later."
            );

            mapper.writeValue(response.getWriter(), errorResponse);
        }
    }
}
