===========================================================
EduTarget Sports - User Management REST API
===========================================================

This is a Spring Boot application (Java 17) that provides a
secure REST API for userRegistration registration and management. The
application follows SOLID principles, uses JWT for security,
and MySQL as the database.

-----------------------------------------------------------
FEATURES
-----------------------------------------------------------
1. User Registration
   - Only POWER_ADMIN can create users.
   - Users have a unique ID in the format "ETU1", "ETU2", etc.
   - Fields: uniqueId, name, role, password, active status.
   - Roles supported: USER, ADMIN, POWER_ADMIN
   - POWER_ADMIN can only be inserted directly into DB.

2. User Management (CRUD)
   - Update userRegistration details (name, role, password [optional]).
   - Activate/Inactivate users instead of deleting them.
   - Delete userRegistration (hard delete - only POWER_ADMIN).
   - Get userRegistration by uniqueId.
   - Get all users.

3. Security
   - JWT-based authentication and authorization.
   - Endpoints protected, only POWER_ADMIN can manage users.
   - Unauthorized requests receive proper error messages.
   - JWT expiration (default: 5 minutes).
   - Custom exceptions for invalid or expired tokens.

4. Validation & Logging
   - Strong password policy enforced.
   - Bean Validation for request payloads.
   - Centralized GlobalExceptionHandler for consistent error responses.
   - Logging (SLF4J + Logback) includes userRegistration performing the action.

5. CORS
   - Enabled to allow access from any client application.

-----------------------------------------------------------
TECH STACK
-----------------------------------------------------------
- Java 17
- Spring Boot 3.x
- Spring Security with JWT
- Hibernate / JPA
- MySQL
- Lombok
- SLF4J Logging
- Maven

-----------------------------------------------------------
SETUP INSTRUCTIONS
-----------------------------------------------------------
1. Clone the repository into your local system.

2. Create a MySQL database:
   > CREATE DATABASE edutargetsports;

3. Insert the initial POWER_ADMIN directly into DB:
   Example query:
   INSERT INTO users (id, unique_id, name, role, password, active)
   VALUES (1, 'ETU1', 'Nagendra Yadav', 'POWER_ADMIN',
   '$2a$10$ENCODED_PASSWORD_HERE', true);

   Note: Use BCryptPasswordEncoder to generate the password hash.



6. Access Swagger UI (if enabled):
   http://localhost:8080/swagger-ui.html

-----------------------------------------------------------
API ENDPOINTS
-----------------------------------------------------------

Authentication:
---------------
POST /api/auth/login
- Request: { "uniqueId": "ETU1", "password": "Admin@123" }
- Response: { "token": "JWT_TOKEN_HERE" }

User Management (requires POWER_ADMIN token):
---------------------------------------------
POST   /api/users        -> Register a new userRegistration
GET    /api/users/{id}   -> Get userRegistration by uniqueId
GET    /api/users        -> Get all users
PUT    /api/users/{id}   -> Update userRegistration details
DELETE /api/users/{id}   -> Delete userRegistration
PATCH  /api/users/{id}/activate   -> Activate userRegistration
PATCH  /api/users/{id}/inactivate -> Inactivate userRegistration

-----------------------------------------------------------
ERROR HANDLING
-----------------------------------------------------------
- 400 BAD REQUEST -> Invalid request (validation, enum mismatch, etc.)
- 401 UNAUTHORIZED -> Invalid or expired JWT
- 403 FORBIDDEN -> Access denied (not a POWER_ADMIN)
- 404 NOT FOUND -> User not found
- 500 INTERNAL SERVER ERROR -> Unhandled exceptions

-----------------------------------------------------------
NOTES
-----------------------------------------------------------
- Password is never returned in API responses (always null).
- Names are automatically capitalized (e.g., "nagendra yadav" -> "Nagendra Yadav").
- Each userRegistration has a "userDisplayed" field in response: [ETU1]-Nagendra Yadav
- All actions are logged with userRegistration ID and name.

===========================================================
