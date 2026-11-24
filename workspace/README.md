# URL Shortener Service

A high-performance URL shortening service built with Java 21 and Spring Boot.

## Prerequisites
- JDK 21
- Maven 3.8+

## Architecture
- **Entity**: `UrlMapping` (originalUrl, shortKey, clickCount, lastAccessed)
- **Service**: Business logic for Base62 encoding and deduplication.
- **Controller**: REST endpoints for interaction.
- **Repository**: Spring Data JPA for persistence.

## API Endpoints

### 1. Shorten URL
`POST /api/shorten`
- **Request**: `{"url": "https://example.com"}`
- **Response**: `200 OK` - `{"shortKey": "aBc123", "originalUrl": "https://example.com"}`

### 2. Redirect
`GET /{shortKey}`
- **Response**: `302 Found` (Redirects to original URL)

### 3. Analytics
`GET /api/stats/{shortKey}`
- **Response**: `200 OK` - `{"shortKey": "aBc123", "originalUrl": "...", "clickCount": 5, "lastAccessed": "..."}`

## Running the Application
```bash
mvn spring-boot:run
```

## Running Tests
```bash
mvn test
```
