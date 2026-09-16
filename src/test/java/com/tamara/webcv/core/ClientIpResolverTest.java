package com.tamara.webcv.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ClientIpResolverTest {

    private final String dummyRemoteAddr = "0.0.0.2";


    private final ClientIpResolver resolver;

    public ClientIpResolverTest() {
        resolver = new ClientIpResolver();
    }

    @Test
    void getClientIpShouldReturnIpFromHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        String dummyIp = "0.0.0.1";
        request.addHeader("X-Forwarded-For", dummyIp);

        String result = resolver.getClientIp(request);

        assertEquals(dummyIp, result);
    }

    @Test
    void getClientIpShouldReturnRemoteAddrIfHeaderIsNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setRemoteAddr(dummyRemoteAddr);

        String result = resolver.getClientIp(request);

        assertEquals(dummyRemoteAddr, result);
    }

    @Test
    void getClientIpShouldReturnRemoteAddrIfHeaderIsBlank() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "  ");
        request.setRemoteAddr(dummyRemoteAddr);

        String result = resolver.getClientIp(request);

        assertEquals(dummyRemoteAddr, result);

    }
}