package com.example.shortener.service;

import com.example.shortener.dto.UrlRequest;
import com.example.shortener.dto.UrlResponse;
import com.example.shortener.entity.UrlMapping;
import com.example.shortener.repository.UrlRepository;
import com.example.shortener.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;

    @Transactional
    public UrlResponse shortenUrl(String originalUrl) {
        return urlRepository.findByOriginalUrl(originalUrl)
                .map(mapping -> new UrlResponse(mapping.getShortKey(), mapping.getOriginalUrl()))
                .orElseGet(() -> {
                    String shortKey = Base62Encoder.generateKey(7);
                    UrlMapping mapping = UrlMapping.builder()
                            .originalUrl(originalUrl)
                            .shortKey(shortKey)
                            .build();
                    urlRepository.save(mapping);
                    return new UrlResponse(shortKey, originalUrl);
                });
    }

    public Optional<String> getOriginalUrl(String shortKey) {
        return urlRepository.findByShortKey(shortKey).map(UrlMapping::getOriginalUrl);
    }
}
