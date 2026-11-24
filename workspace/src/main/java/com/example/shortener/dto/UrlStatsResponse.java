package com.example.shortener.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UrlStatsResponse {
    private String shortKey;
    private String originalUrl;
    private long clickCount;
    private LocalDateTime lastAccessed;
}
