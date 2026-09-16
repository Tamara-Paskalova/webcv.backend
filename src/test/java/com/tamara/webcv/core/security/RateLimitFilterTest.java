package com.tamara.webcv.core.security;

import com.tamara.webcv.core.ClientIpResolver;
import com.tamara.webcv.core.Constants;
import com.tamara.webcv.core.service.RateLimiterService;
import com.tamara.webcv.exception.RateLimitExceededException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    @Mock
    private RateLimiterService rateLimiterService;

    @Mock
    private ClientIpResolver ipResolver;

    @Spy
    private ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private RateLimitFilter rateLimitFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @Test
    void shouldNotFilterWhenGetRequest() throws ServletException, IOException {
        request.setMethod("GET");
        request.setRequestURI(Constants.MESSAGE_FULL_ENDPOINT);

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(rateLimiterService);
    }

    @Test
    void doFilterInternalSuccessWhenLimitNotExceeded() throws ServletException, IOException {
        String dummyIp = "192.168.1.1";
        request.setMethod("POST");
        request.setRequestURI(Constants.MESSAGE_FULL_ENDPOINT);

        when(ipResolver.getClientIp(request)).thenReturn(dummyIp);
        when(rateLimiterService.checkLimit(dummyIp)).thenReturn(true);

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void doFilterInternalError429WhenLimitExceeded() throws ServletException, IOException {
        String dummyIp = "192.168.1.1";
        request.setMethod("POST");
        request.setRequestURI(Constants.MESSAGE_FULL_ENDPOINT);

        when(ipResolver.getClientIp(request)).thenReturn(dummyIp);
        doThrow(new RateLimitExceededException("Limit exceeded"))
                .when(rateLimiterService).checkLimit(dummyIp);

        rateLimitFilter.doFilter(request, response, filterChain);

        verifyNoInteractions(filterChain);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String content = response.getContentAsString();
        assertTrue(content.contains("MESSAGE_LIMIT_EXCEEDED"));
        assertTrue(content.contains("Can't handle your request. Please retry later."));
    }
}
