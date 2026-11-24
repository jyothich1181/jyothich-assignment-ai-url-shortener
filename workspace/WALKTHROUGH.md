The URL Shortener service has been successfully configured and verified.

### Summary of Changes
1.  **Project Configuration**: Updated `pom.xml` to include `lombok` as a dependency and configured the `spring-boot-maven-plugin` to properly handle annotation processing. This resolved compilation errors where Lombok-generated methods (getters, builders) were not being recognized.
2.  **Documentation**: Created a comprehensive `README.md` detailing the architecture, API endpoints, and instructions for running the application and tests.
3.  **Verification**: Ran the full test suite (`mvn test`), which includes both `UrlControllerTest` (integration) and `UrlServiceTest` (unit). All tests passed successfully.

### Test Coverage
*   **`UrlControllerTest`**: Validates API endpoints (`/api/shorten`, `/api/stats/{shortKey}`) and input validation (e.g., rejecting invalid URLs).
*   **`UrlServiceTest`**: Validates core business logic, including:
    *   URL shortening and deduplication.
    *   Retrieval of original URLs.
    *   Click count incrementing.
    *   Analytics data retrieval.

The application is now fully functional and ready for deployment.