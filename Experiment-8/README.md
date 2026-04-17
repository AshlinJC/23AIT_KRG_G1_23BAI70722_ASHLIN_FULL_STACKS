# LivePoll System — Experiment 8

A secure full-stack polling application built with Spring Boot (backend) and React (frontend).

## Project Structure

```
livepoll/
├── backend/       Spring Boot + Spring Security + JWT + OAuth2
└── frontend/      React + Vite + Axios
```

---

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 18+

---

### 1. Run the Backend

```bash
cd backend
mvn spring-boot:run
```

The backend starts on **http://localhost:8080**

On startup, sample data is seeded automatically:

| Role  | Email                 | Password  |
|-------|-----------------------|-----------|
| ADMIN | admin@livepoll.com    | admin123  |
| USER  | user@livepoll.com     | user123   |

H2 Console (in-memory DB viewer): http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:livepolldb`
- Username: `sa` / Password: *(empty)*

---

### 2. Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on **http://localhost:5173**

---

## API Reference

### Auth Endpoints (public)
| Method | URL                    | Body / Params            |
|--------|------------------------|--------------------------|
| POST   | /api/auth/login        | `{ email, password }`    |
| POST   | /api/auth/register     | `{ name, email, password }` |
| GET    | /api/auth/me           | Bearer token required    |

### Poll Endpoints
| Method | URL                          | Auth Required  |
|--------|------------------------------|----------------|
| GET    | /api/polls                   | None           |
| GET    | /api/polls/{id}              | None           |
| POST   | /api/polls                   | ADMIN          |
| POST   | /api/polls/{id}/vote?optionId=X | USER or ADMIN |
| PUT    | /api/polls/{id}/toggle       | ADMIN          |
| DELETE | /api/polls/{id}              | ADMIN          |

### Admin Endpoints (all require ADMIN role)
| Method | URL               |
|--------|-------------------|
| GET    | /api/admin/users  |
| GET    | /api/admin/polls  |
| GET    | /api/admin/stats  |

---

## Google OAuth Setup (optional)

1. Go to https://console.cloud.google.com → APIs & Services → Credentials
2. Create OAuth 2.0 Client ID → Web application
3. Add Authorised redirect URI: `http://localhost:8080/login/oauth2/code/google`
4. Edit `backend/src/main/resources/application.yml`:
   ```yaml
   spring:
     security:
       oauth2:
         client:
           registration:
             google:
               client-id: YOUR_CLIENT_ID
               client-secret: YOUR_CLIENT_SECRET
   ```

---

## Security Architecture

```
React                  Spring Boot
  |                       |
  |-- POST /api/auth/login --> AuthController
  |                       |-- AuthenticationManager
  |                       |-- BCrypt password check
  |                       |-- JwtTokenProvider.generateToken()
  |<-- { token, roles } --|
  |                       |
  |-- GET /api/polls ---> JwtAuthenticationFilter
  |   Authorization:      |-- validateToken()
  |   Bearer <token>      |-- SecurityContextHolder.setAuthentication()
  |                       |-- PollController (@PreAuthorize)
  |<-- 200 polls ---------|
  |                       |
  |-- POST /api/admin --> |-- hasRole('ADMIN') check
  |   (USER role)         |-- 403 FORBIDDEN
  |<-- 403 Forbidden -----|
```

---

## Technologies Used

| Layer     | Technology                        |
|-----------|-----------------------------------|
| Backend   | Spring Boot 3.2, Java 17          |
| Security  | Spring Security 6, JWT (jjwt)     |
| OAuth     | Spring OAuth2 Client (Google)     |
| Database  | H2 (in-memory), Spring Data JPA   |
| Frontend  | React 18, Vite, React Router v6   |
| HTTP      | Axios with JWT interceptor        |
