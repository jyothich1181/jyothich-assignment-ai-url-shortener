# URL Shortener Service

A high-performance URL shortening REST service built with **Java 21** and **Spring Boot 3**, featuring Base62 short-key generation, click analytics, idempotent URL deduplication, and strict input validation.

---

## Prerequisites
- **JDK 21+**
- **Maven 3.8+**

---

## Architecture
- **Entity (`UrlMapping`):** JPA entity storing `id`, `originalUrl` (unique), `shortKey` (unique), `clickCount`, and `lastAccessed`.
- **Repository (`UrlMappingRepository`):** Spring Data JPA repository supporting lookups by `shortKey` and `originalUrl`.
- **Service (`UrlShortenerService`):** Handles HTTP/HTTPS URL validation, deduplication checks, Base62 short-key generation, and atomic click/timestamp updates on redirect.
- **Controller (`UrlController`):** Exposes REST endpoints for shortening URLs, redirecting short keys, and retrieving analytics.

---

## Core Business Rules (Scenarios 1–3)
- **Idempotent Deduplication:** Submitting an `originalUrl` that already exists in the database returns the existing `shortKey` rather than creating a duplicate record.
- **Strict URL Validation:** Only valid `http://` or `https://` URLs are accepted. Blank, null, or malformed URLs are rejected with `400 Bad Request`.
- **Click Analytics:** Every successful `GET /{shortKey}` redirect increments `clickCount` by `1` and updates `lastAccessed` to the current timestamp.

---

## API Endpoints & Examples

### 1. Shorten a URL
`POST /api/shorten`

* **Request:**
  ```bash
  curl -X POST http://localhost:8080/api/shorten \
    -H "Content-Type: application/json" \
    -d '{"url": "https://example.com"}'
  ```
* **Response (`200 OK`):**
  ```json
  {
    "shortKey": "aBc123",
    "originalUrl": "https://example.com"
  }
  ```
* **Error Responses:**
  * `400 Bad Request` — Returned if `url` is blank, missing, or not a valid HTTP/HTTPS URL.

---

### 2. Redirect to Original URL
`GET /{shortKey}`

* **Request:**
  ```bash
  curl -i http://localhost:8080/aBc123
  ```
* **Response (`302 Found`):**
  Redirects the client via the `Location: https://example.com` response header while incrementing `clickCount` and updating `lastAccessed`.
* **Error Responses:**
  * `404 Not Found` — Returned if `{shortKey}` does not exist.

---

### 3. View Click Analytics
`GET /api/stats/{shortKey}`

* **Request:**
  ```bash
  curl http://localhost:8080/api/stats/aBc123
  ```
* **Response (`200 OK`):**
  ```json
  {
    "shortKey": "aBc123",
    "originalUrl": "https://example.com",
    "clickCount": 5,
    "lastAccessed": "2026-11-23T19:45:00"
  }
  ```
* **Error Responses:**
  * `404 Not Found` — Returned if `{shortKey}` does not exist.

---

## Running the Application & Tests

### Start the Spring Boot Server
```bash
mvn spring-boot:run
```
*(By default, the service starts on `http://localhost:8080` using an in-memory H2 database.)*

### Run the JUnit 5 & MockMvc Test Suite
```bash
mvn test
```
