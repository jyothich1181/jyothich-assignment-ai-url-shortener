### Analysis and Engineering Assumptions

**Ambiguities:**
*   **Validation scope:** Does "input URL validation" mean basic format checking (URL structure) or reachability checks?
*   **Constraint enforcement:** Does "duplicate long URLs" imply a unique constraint at the database level?
*   **Redirection behavior:** Should the system treat all identical URLs as the exact same resource with shared stats, or are they tracked independently?

**Engineering Assumptions:**
1.  **Validation:** I will use Jakarta Validation (`@URL`) for format validation to ensure standard URI compliance.
2.  **Deduplication:** I will add a `UNIQUE` constraint on the `longUrl` field in the `UrlMapping` entity and use a `findOptionalByLongUrl` lookup in the service layer to prevent creation of redundant database rows.
3.  **Performance:** I will index the `longUrl` column to ensure lookup speed remains O(log N) as the dataset grows.
4.  **README:** I will create a `README.md` that covers architecture, API endpoints, and setup instructions.

---

### Implementation Plan (PLAN.md)

# Implementation Plan: Deduplication and Validation

## 1. Entity & Repository Updates
- [ ] Add `@Column(unique = true)` to `longUrl` in `UrlMapping.java`.
- [ ] Add `Optional<UrlMapping> findByLongUrl(String longUrl)` to `UrlRepository.java`.

## 2. Service Layer Refactoring
- [ ] Update `UrlService.shortenUrl` to check for existence of `longUrl` before generating a new code.
- [ ] Throw `jakarta.validation.ConstraintViolationException` or handle via `@Valid` in Controller.

## 3. Controller Layer
- [ ] Add `@Valid` to the `UrlRequest` parameter in `UrlController.shortenUrl`.
- [ ] Add `@URL` annotation to the `longUrl` field in `UrlRequest.java`.

## 4. Testing
- [ ] Add unit test in `UrlServiceTest` to verify that shortening the same URL twice returns the same code.
- [ ] Add `UrlControllerTest` (MockMvc) to verify `400 Bad Request` for invalid URL formats.

## 5. Documentation
- [ ] Create `README.md` with:
    - Project Overview.
    - Architecture details.
    - API usage.
    - How to run tests.

---

### Step-by-Step Execution

#### Step 1: Updating DTO and Entity
*   `src/main/java/com/example/shortener/dto/UrlRequest.java`: Add `@URL` and `@NotBlank`.
*   `src/main/java/com/example/shortener/entity/UrlMapping.java`: Add unique constraint.

#### Step 2: Repository Update
*   Ensure `UrlRepository` has the lookup method.

#### Step 3: Service Logic
```java
public String shortenUrl(String longUrl) {
    return repository.findByLongUrl(longUrl)
        .map(UrlMapping::getShortCode)
        .orElseGet(() -> {
            String code = encoder.encode(System.currentTimeMillis());
            // save...
            return code;
        });
}
```

#### Step 4: Testing
*   Run `mvn test` to verify no regressions.

#### Step 5: README.md Creation
*   Draft the documentation based on the Spring Boot implementation details.

---

**Would you like me to proceed with applying these changes to the codebase?**