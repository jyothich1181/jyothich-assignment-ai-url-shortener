# URL Shortener Implementation Plan

## Tasks
1. [ ] Setup Entity `UrlMapping` with JPA annotations.
2. [ ] Implement `UrlRepository`.
3. [ ] Develop `UrlService` with base62 logic for key generation.
4. [ ] Build `UrlController` with endpoints:
    - POST `/api/shorten`
    - GET `/{shortKey}`
5. [ ] Implement validation DTOs.
6. [ ] Write `UrlServiceTest` (Unit).
7. [ ] Write `UrlControllerTest` (Integration).
8. [ ] Execute `run_maven_tests` to verify compliance.

## Assumptions
- H2 Database will be used.
- Keys are 7 characters long.
- 302 Found is used for redirection.
