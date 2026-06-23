# Task Service — Hashclick Microservices

> **Training Program | Week 5**
> Java Developer — Jeevan Kumar Gujja

---

## Overview

The Task Service is an independent Spring Boot microservice responsible for task management. It handles full task CRUD, role-based access control (RBAC), overdue detection, and scheduled alerts. It communicates with the **User Service** via REST to fetch user details for authorization decisions.

---

## Tech Stack

| Layer        | Technology                          |
|--------------|-------------------------------------|
| Language     | Java 17                             |
| Framework    | Spring Boot 3.2.5                   |
| Security     | Spring Security + JWT (JJWT 0.11.5) |
| Database     | H2 (default) / MySQL (via env vars) |
| ORM          | Spring Data JPA / Hibernate         |
| Build Tool   | Maven                               |
| API Docs     | Swagger / OpenAPI 3 (springdoc)     |
| HTTP Client  | RestTemplate (calls user-service)   |
| Deployment   | Docker                              |

---

## Port

```
http://localhost:8082
```

---

## API Endpoints

### Tasks (JWT token required)

| Method | Endpoint                  | Description                                  |
|--------|---------------------------|----------------------------------------------|
| POST   | `/api/tasks`              | Create a new task                            |
| GET    | `/api/tasks`              | Get all tasks (admin: all, user: own)        |
| GET    | `/api/tasks/{id}`         | Get task by ID                               |
| GET    | `/api/tasks/my`           | Get tasks assigned to me                     |
| GET    | `/api/tasks/overdue`      | Get overdue tasks (filtered by role)         |
| GET    | `/api/tasks/status/{s}`   | Filter tasks by status                       |
| PUT    | `/api/tasks/{id}`         | Update full task (creator or admin)          |
| PATCH  | `/api/tasks/{id}/status`  | Update task status                           |
| PATCH  | `/api/tasks/{id}/assign`  | Assign task to a user (ADMIN only)           |
| DELETE | `/api/tasks/{id}`         | Delete task (creator or admin)               |
| GET    | `/api/tasks/stats`        | Dashboard statistics (ADMIN only)            |

---

## Access Control (RBAC)

| Action              | ROLE_USER    | ROLE_ADMIN |
|---------------------|--------------|------------|
| Create task         | Own          | All        |
| View tasks          | Own only     | All tasks  |
| Update task         | Own only     | Any task   |
| Delete task         | Own only     | Any task   |
| Assign task to user | No           | Yes        |
| View overdue tasks  | Own only     | All tasks  |
| View stats          | No           | Yes        |

---

## How to Run Locally

> **Prerequisite:** User Service must be running on port 8081 first.

```bash
mvn clean package -DskipTests
java -jar target/task-service-1.0.0.jar
```

| URL | Description |
|-----|-------------|
| `http://localhost:8082/swagger-ui.html` | Swagger API Docs |
| `http://localhost:8082/h2-console`      | H2 Database Console |

### H2 Console Settings
```
JDBC URL:  jdbc:h2:mem:taskdb
Username:  sa
Password:  (leave empty)
```

---

## Docker

```bash
docker build -t task-service .
docker run -p 8082:8082 -e USER_SERVICE_URL=http://localhost:8081 task-service
```

---

## Inter-Service Communication

This service calls **user-service** via REST on every authenticated request:

1. JWT is validated locally (shared secret — no network call)
2. `GET http://localhost:8081/api/users/email/{email}` — fetches user ID and role
3. Role is used for all RBAC decisions in TaskService

The original Bearer token is forwarded in the Authorization header when calling user-service.

---

## Environment Variables

| Variable           | Default                       | Description                  |
|--------------------|-------------------------------|------------------------------|
| `user.service.url` | `http://localhost:8081`       | Base URL of user-service     |
| `jwt.secret`       | (set in application.properties)| Must match user-service      |
| `server.port`      | `8082`                        | Service port                 |

---

## Related Service

- **user-service** → https://github.com/jeevankumargujja/user-service

---

## Author

**Jeevan Kumar Gujja**
Java Developer
Hashclick Solutions LLC
