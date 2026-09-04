package com.tamara.webcv.core;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class ClientIpResolver {
    public String getClientIp(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");

        if (header != null && !header.isBlank()) {
            return header.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
