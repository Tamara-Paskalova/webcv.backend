package com.tamara.webcv.core.service;

import com.tamara.webcv.exception.TokenValidationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnstileServiceTest {

    private final String token = "tokenabvcde123456789";
    private final String clientIp = "clientIpabvcde123456789";

    private HttpResponse<String> response;

    @Mock
    private HttpClient client;

    private final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private TurnstileService service;


    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "secretKey", "secretKeyabvcde123456789");
        ReflectionTestUtils.setField(service, "mapper", mapper);

        response = Mockito.mock(HttpResponse.class);
    }


    @Test
    void verifyReturnsTrueWhenTokenIsValid() throws Exception {
        when(response.body()).thenReturn("""
            {
                "success": true
            }
            """);
        when(client.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        )).thenReturn(response);

        assertTrue(service.verify(token, clientIp));
    }

    @Test
    void verifyReturnsFalseWhenTokenIsInvalid() throws Exception {
        when(response.body()).thenReturn("""
            {
                "success": false
            }
            """);
        when(client.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        )).thenReturn(response);

        assertThrows(TokenValidationException.class, () -> service.verify(token, clientIp));
    }

    @Test
    void verifyThrowsExceptionWhenTokenIsNull() {
        assertThrows(TokenValidationException.class, () -> service.verify(null, null));
    }

    @Test
    void verifyThrowsExceptionWhenTokenIsBlank() {
        assertThrows(TokenValidationException.class, () -> service.verify("   ", null));
    }
}