package com.example.shortener.repository;

import com.example.shortener.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlMapping, Long> {
    Optional<UrlMapping> findByShortKey(String shortKey);
    Optional<UrlMapping> findByOriginalUrl(String originalUrl);
}
