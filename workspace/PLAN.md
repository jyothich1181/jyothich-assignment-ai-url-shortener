To implement click analytics for the URL shortener, I have analyzed the requirements and established the following engineering plan.

### 1. Ambiguities & Assumptions
*   **Ambiguity:** Should I track individual access events (audit log) or just aggregate values? 
    *   **Assumption:** Given the requirement for "total clicks and last accessed," I will track aggregate counters and a timestamp on the `UrlMapping` entity to avoid massive table growth.
*   **Ambiguity:** Performance impact of incrementing on every redirect.
    *   **Assumption:** I will update the entity directly during the redirect flow. For high-scale production, this would eventually require an asynchronous approach (e.g., Kafka or Redis), but for this task, I will stick to synchronous persistence to ensure data consistency.

### 2. Implementation Plan

#### Step 1: Update Domain Model
*   Modify `UrlMapping.java` to add `long clickCount` and `LocalDateTime lastAccessed`.
*   Add a migration path or update the existing entity to include these fields (JPA will auto-update if `ddl-auto` is set, otherwise note the need for schema change).

#### Step 2: Repository Update
*   Ensure `UrlRepository` supports custom finders if needed (not strictly necessary as we already have `findByShortCode`).

#### Step 3: Service Layer Enhancement
*   Update `UrlService.shortenUrl` to initialize `clickCount` to 0.
*   Implement `recordClick(String shortCode)` method to increment the counter and update the timestamp.
*   Implement `getStats(String shortCode)` method to fetch the aggregate data.

#### Step 4: Controller Update
*   Update `UrlController` to call `recordClick` inside the redirection method.
*   Create a new `GET /api/stats/{shortCode}` endpoint to return the analytics.

#### Step 5: Data Transfer Objects (DTOs)
*   Create `UrlStatsResponse.java` to encapsulate `shortCode`, `originalUrl`, `clickCount`, and `lastAccessed`.

#### Step 6: Testing
*   Write unit tests for `UrlService` to verify the increment logic.
*   Write integration tests using `MockMvc` to verify the redirect triggers the increment and the stats endpoint returns accurate data.

---

### 3. Detailed Tasks (PLAN.md)

```markdown
# Analytics Implementation Plan

- [ ] **Step 1: Entity Update**
    - Add `private long clickCount;` and `private LocalDateTime lastAccessed;` to `UrlMapping`.
- [ ] **Step 2: DTO Creation**
    - Create `UrlStatsResponse` containing fields for stats reporting.
- [ ] **Step 3: Service Logic**
    - Update `UrlService` to handle `recordClick` (atomic increment logic).
    - Add `getStats` method to retrieve analytics.
- [ ] **Step 4: Controller Endpoints**
    - Modify the redirect endpoint in `UrlController` to trigger `recordClick`.
    - Expose `GET /api/stats/{shortCode}`.
- [ ] **Step 5: Testing**
    - Update `UrlServiceTest` to cover increment logic.
    - Create `UrlControllerIntegrationTest` to verify the full flow (Redirect -> Stats update).
```

I will now proceed with these changes. I'll start by reading the current files to ensure I have the full context before modifying.