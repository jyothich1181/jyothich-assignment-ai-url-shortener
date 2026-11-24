package com.example.shortener.controller;

import com.example.shortener.dto.UrlRequest;
import com.example.shortener.dto.UrlResponse;
import com.example.shortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/api/shorten")
    public ResponseEntity<UrlResponse> shorten(@RequestBody UrlRequest request) {
        return ResponseEntity.ok(urlService.shortenUrl(request.url()));
    }

    @GetMapping("/{shortKey}")
    public void redirect(@PathVariable String shortKey, HttpServletResponse response) throws IOException {
        String originalUrl = urlService.getOriginalUrl(shortKey)
                .orElseThrow(() -> new RuntimeException("URL not found"));
        response.sendRedirect(originalUrl);
    }
}
