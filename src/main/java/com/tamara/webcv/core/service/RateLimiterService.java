package com.tamara.webcv.core.service;

import com.tamara.webcv.core.ClientRequest;
import com.tamara.webcv.exception.RateLimitExceededException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {
    private final Map<String, ClientRequest> ipCache = new ConcurrentHashMap<>();
    
    private final long rateLimitWindowHours = 24;
    private final int permittedDailyRequestCount = 5;
    private final long permittedMinutesFromLastRequest = 1;

    public void checkLimit(String ip) {
        isLimitExceeded(ip);
    }

    protected boolean isLimitExceeded(String ip) {
        LocalDateTime now = LocalDateTime.now();

        ClientRequest clientRequest = ipCache.get(ip);

        if (clientRequest == null) {
            ipCache.put(ip, new ClientRequest(ip, now, now, 1));
            return false;
        }

        if (isCoolDownOver(clientRequest, now)) {
            clientRequest.setWindowStart(now);
            clientRequest.setLastRequestTime(now);
            clientRequest.setRequestCount(1);
            return false;
        }

        if (isDailyRequestCountLimitExceeded(clientRequest)) {
            throw new RateLimitExceededException("Daily limit count for messages exceeded for IP %s".formatted(ip));
        }

        if (!oneMinutePassed(clientRequest, now)) {
            throw new RateLimitExceededException("Count of messages for minute exceeded for IP %s".formatted(ip));
        }

        clientRequest.setLastRequestTime(now);
        clientRequest.setRequestCount(clientRequest.getRequestCount() + 1);

        return false;
    }

    private boolean isDailyRequestCountLimitExceeded(ClientRequest clientRequest) {
        int requestsCount = clientRequest.getRequestCount();
        return requestsCount >= permittedDailyRequestCount;
    }

    private boolean oneMinutePassed(ClientRequest clientRequest, LocalDateTime now) {
        long minutesFromLastRequest = ChronoUnit.MINUTES.between(clientRequest.getLastRequestTime(), now);
        return minutesFromLastRequest >= permittedMinutesFromLastRequest;

    }

    private boolean isCoolDownOver(ClientRequest request, LocalDateTime now) {
        return ChronoUnit.HOURS.between(request.getWindowStart(), now) >= rateLimitWindowHours;
    }
}
