package com.tamara.webcv.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {
    private String ip;
    private LocalDateTime windowStart;
    private LocalDateTime lastRequestTime;
    private int requestCount;
}
