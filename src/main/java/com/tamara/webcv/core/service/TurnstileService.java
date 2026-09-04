package com.tamara.webcv.core.service;

import com.tamara.webcv.exception.TokenValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class TurnstileService {
    private static final String SITEVERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    @Value("${turnstile.secret-key}")
    private String secretKey;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private ObjectMapper mapper;

    public boolean verify(String token, String clientIp) {

        if (token == null || token.isBlank()) {
            throw new TokenValidationException("The verification token sent from IP: %s is null".formatted(clientIp));
        }

        String encodedKey = URLEncoder.encode(secretKey, StandardCharsets.UTF_8);
        String encodedSecret = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String encodedClientIp = clientIp != null ? URLEncoder.encode(clientIp, StandardCharsets.UTF_8): "";

        String form = """
                secret=%s&response=%s&remoteip=%s""".formatted(encodedKey, encodedSecret, encodedClientIp);
        boolean verified = false;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SITEVERIFY_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode body = mapper.readTree(response.body());
            verified = body.path("success").asBoolean(false);
            if (!verified) {
                throw new TokenValidationException("Invalid verification token from IP: %s".formatted(clientIp));
            }
        } catch (Exception e) {
            throw new TokenValidationException("Unable to process token validation", e);
        }

        return verified;
    }
}
