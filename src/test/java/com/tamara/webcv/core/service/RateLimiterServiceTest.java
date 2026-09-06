package com.tamara.webcv.core.service;

import com.tamara.webcv.core.ClientRequest;
import com.tamara.webcv.exception.RateLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RateLimiterServiceTest {

    private final String ip = "0.0.0.0";
    private final String dailyLimitExceptionMessage = "Daily limit count for messages exceeded for IP %s".formatted(ip);
    private final String oneMinuteLimitExceptionMessage = "Count of messages for minute exceeded for IP %s".formatted(ip);
    private final Map<String, ClientRequest> ipCache = new ConcurrentHashMap<>();

    private RateLimiterService service;

    @BeforeEach
    void init() {
        service = new RateLimiterService();
        ReflectionTestUtils.setField(service, "ipCache", ipCache);
    }

    @Test
    void checkLimitReturnsFalseWhenLimitIsNotExceeded() {
        assertFalse(service.checkLimit(ip));
    }

    @Test
    void checkLimitThrowsExceptionWhenDailyCountLimitExceeded() {
        ClientRequest request = new ClientRequest();
        LocalDateTime windowTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        request.setWindowStart(windowTime);
        request.setRequestCount(5);
        request.setIp(ip);
        ipCache.put(ip, request);

        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, () -> service.checkLimit(ip));

        assertEquals(dailyLimitExceptionMessage, exception.getMessage());
    }

    @Test
    void checkLimitsThrowsExceptionWhenTimeLimitExceeded() {
        ClientRequest request = new ClientRequest();
        LocalDateTime halfAMinuteBefore = LocalDateTime.now().minusSeconds(30);
        request.setWindowStart(halfAMinuteBefore);
        request.setRequestCount(2);
        request.setLastRequestTime(halfAMinuteBefore);
        request.setIp(ip);
        ipCache.put(ip, request);

        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, () -> service.checkLimit(ip));
        assertEquals(oneMinuteLimitExceptionMessage, exception.getMessage());
    }

    @Test
    void checkLimitReturnsFalseIfADayIsPassed() {
        ClientRequest request = new ClientRequest();
        LocalDateTime oneDayBefore = LocalDateTime.now().minusDays(1);
        request.setIp(ip);
        request.setLastRequestTime(oneDayBefore);
        request.setRequestCount(1);
        request.setWindowStart(oneDayBefore);
        ipCache.put(ip, request);

        assertFalse(service.checkLimit(ip));
    }

    @Test
    void checkLimitReturnsFalseIfMoreThanAMinutePassed() {
        ClientRequest request = new ClientRequest();
        LocalDateTime twoMinutesBefore = LocalDateTime.now().minusMinutes(2);
        request.setWindowStart(twoMinutesBefore);
        request.setRequestCount(1);
        request.setLastRequestTime(twoMinutesBefore);
        request.setIp(ip);
        ipCache.put(ip, request);

        assertFalse(service.checkLimit(ip));
    }
}