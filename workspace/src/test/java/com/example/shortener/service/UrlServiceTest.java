package com.example.shortener.service;

import com.example.shortener.dto.UrlResponse;
import com.example.shortener.entity.UrlMapping;
import com.example.shortener.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    @Test
    void testShortenUrl_New() {
        when(urlRepository.findByOriginalUrl("http://example.com")).thenReturn(Optional.empty());
        
        UrlResponse response = urlService.shortenUrl("http://example.com");
        
        assertNotNull(response.shortKey());
        assertEquals(7, response.shortKey().length());
        verify(urlRepository, times(1)).save(any(UrlMapping.class));
    }

    @Test
    void testShortenUrl_Existing() {
        UrlMapping mapping = new UrlMapping(1L, "http://example.com", "abc1234", null);
        when(urlRepository.findByOriginalUrl("http://example.com")).thenReturn(Optional.of(mapping));
        
        UrlResponse response = urlService.shortenUrl("http://example.com");
        
        assertEquals("abc1234", response.shortKey());
        verify(urlRepository, never()).save(any());
    }
}
