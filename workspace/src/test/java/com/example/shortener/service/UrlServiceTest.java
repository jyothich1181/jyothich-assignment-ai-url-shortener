package com.example.shortener.service;

import com.example.shortener.dto.UrlResponse;
import com.example.shortener.dto.UrlStatsResponse;
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
        UrlMapping mapping = UrlMapping.builder()
                .id(1L)
                .originalUrl("http://example.com")
                .shortKey("abc1234")
                .clickCount(0)
                .build();
        when(urlRepository.findByOriginalUrl("http://example.com")).thenReturn(Optional.of(mapping));
        
        UrlResponse response = urlService.shortenUrl("http://example.com");
        
        assertEquals("abc1234", response.shortKey());
        verify(urlRepository, never()).save(any());
    }

    @Test
    void testGetOriginalUrl_IncrementsClickCount() {
        UrlMapping mapping = UrlMapping.builder()
                .id(1L)
                .originalUrl("http://example.com")
                .shortKey("abc1234")
                .clickCount(0)
                .build();
        when(urlRepository.findByShortKey("abc1234")).thenReturn(Optional.of(mapping));
        
        Optional<String> url = urlService.getOriginalUrl("abc1234");
        
        assertTrue(url.isPresent());
        assertEquals(1, mapping.getClickCount());
        assertNotNull(mapping.getLastAccessed());
        verify(urlRepository).save(mapping);
    }

    @Test
    void testGetStats() {
        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("http://example.com")
                .shortKey("abc1234")
                .clickCount(5)
                .build();
        when(urlRepository.findByShortKey("abc1234")).thenReturn(Optional.of(mapping));
        
        Optional<UrlStatsResponse> stats = urlService.getStats("abc1234");
        
        assertTrue(stats.isPresent());
        assertEquals(5, stats.get().getClickCount());
    }
}
