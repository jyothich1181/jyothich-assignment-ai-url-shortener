package com.example.shortener.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String originalUrl;

    @Column(nullable = false, unique = true, length = 10)
    private String shortKey;

    private LocalDateTime createdAt;

    private long clickCount = 0;

    private LocalDateTime lastAccessed;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
