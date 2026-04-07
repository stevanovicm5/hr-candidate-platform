# HR Candidate Platform

A modern REST API for managing job candidates and their technical skills. Built with Spring Boot 4, Java 21, and PostgreSQL.

## Tech Stack

| Component | Version |
|-----------|---------|
| Java | 21 LTS |
| Spring Boot | 4.0.5 |
| Spring Data JPA | Latest |
| PostgreSQL | 16 |
| Docker | Latest |
| Maven | 3.9+ |

**Additional Libraries:**
- Lombok (reduces boilerplate)
- SpringDoc OpenAPI 3.0.2 (API documentation)
- JUnit 5 + Mockito (testing)
- Jakarta Validation (input validation)

---

## Project Structure

```
src/main/java/com/intens/hr_platform/
├── HrPlatformApplication.java          # Main application entry point
│
├── controller/                          # REST endpoints (HTTP handlers)
│   ├── CandidateController.java        # Candidate endpoints
│   └── SkillController.java            # Skill endpoints
│
├── service/                             # Business logic layer
│   ├── CandidateService.java           # Candidate interface
│   ├── CandidateServiceImpl.java        # Candidate implementation
│   ├── SkillService.java               # Skill interface
│   └── SkillServiceImpl.java            # Skill implementation
│
├── repository/                          # Data access layer (JPA)
│   ├── CandidateRepository.java        # Candidate database queries
│   └── SkillRepository.java            # Skill database queries
│
├── entity/                              # JPA entities (database models)
│   ├── Candidate.java                  # Candidate entity
│   └── Skill.java                      # Skill entity
│
├── dto/                                 # Data Transfer Objects
│   ├── candidate/
│   │   ├── CandidateRequestDto.java    # Request payload
│   │   ├── CandidateResponseDto.java   # Response payload
│   │   └── CandidateUpdateRequestDto.java # PATCH payload
│   │
│   └── skill/
│       ├── SkillRequestDto.java        # Skill request
│       └── SkillResponseDto.java       # Skill response
│
├── mapper/                              # Entity ↔ DTO conversions
│   ├── CandidateMapper.java            # Candidate mapping
│   └── SkillMapper.java                # Skill mapping
│
└── exception/                           # Error handling
    ├── DuplicateResourceException.java # 409 Conflict
    ├── ResourceNotFoundException.java  # 404 Not Found
    └── GlobalExceptionHandler.java     # Centralized error handler
```

---

## API Endpoints

### Candidates Management

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/api/candidates` | Get all candidates | 200 OK |
| POST | `/api/candidates` | Create new candidate | 201 Created |
| PATCH | `/api/candidates/{id}` | Update candidate | 200 OK |
| DELETE | `/api/candidates/{id}` | Delete candidate | 204 No Content |
| GET | `/api/candidates/search?name=<name>` | Search by name | 200 OK |
| GET | `/api/candidates/search?email=<email>` | Search by email | 200 OK |
| GET | `/api/candidates/search?skills=<skill1>&skills=<skill2>` | Search by skills | 200 OK |
| POST | `/api/candidates/{id}/skills/{skillId}` | Add skill to candidate | 200 OK |
| DELETE | `/api/candidates/{id}/skills/{skillId}` | Remove skill from candidate | 200 OK |

### Skills Management

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| GET | `/api/skills` | Get all skills | 200 OK |
| POST | `/api/skills` | Create new skill | 201 Created |
| PATCH | `/api/skills/{id}` | Update skill | 200 OK |
| DELETE | `/api/skills/{id}` | Delete skill | 204 No Content |

---

## Quick Start

### Prerequisites

- Java 21 LTS
- Maven 3.9+
- PostgreSQL 16 (local or Docker)
- Docker & Docker Compose (optional)

### 1. Environment Setup

Copy the example environment file and update with your values:

```bash
cp .env.example .env
```

**`.env` file contents:**
```env
# Database connection (for local development)
DB_URL=jdbc:postgresql://localhost:5432/hr_platform
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Docker PostgreSQL environment
POSTGRES_DB=hr_platform
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
```

### 2. Running Locally

**Option A: IDE**
```bash
./mvnw clean spring-boot:run
```

**Option B: Build and run JAR**
```bash
./mvnw clean package -DskipTests
java -jar target/hr-platform-0.0.1-SNAPSHOT.jar
```

**Access:**
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/v3/api-docs`

### 3. Running with Docker Compose

Complete stack with PostgreSQL (recommended for testing):

```bash
docker-compose down        # Clean up old containers (if any)
docker-compose up --build  # Build and run
```

**Services:**
- App: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

Monitor logs:
```bash
docker-compose logs -f app
```

### 4. Running Tests

```bash
./mvnw clean test
```

Results:
- **67 tests** - 100% pass rate
- **Coverage:** Controllers, Services, Repositories
- **Framework:** JUnit 5 + Mockito

---

## Example API Requests

### Create Candidate
```bash
POST /api/candidates
Content-Type: application/json

{
  "fullName": "Milan Stevanovic",
  "email": "milan@example.com",
  "contactNumber": "+381621234567",
  "dateOfBirth": "1995-05-15",
  "skillIds": [1, 2]
}
```

### Search Candidates by Name
```bash
GET /api/candidates/search?name=Milan
```

### Search by Skills
```bash
GET /api/candidates/search?skills=Java&skills=Spring
```

### Add Skill to Candidate
```bash
POST /api/candidates/1/skills/3
```

### Create Skill
```bash
POST /api/skills
Content-Type: application/json

{
  "name": "Kubernetes"
}
```

---

## Validation Rules

### Candidate Fields
| Field | Rules |
|-------|-------|
| fullName | Required, 2-50 chars, no leading/trailing spaces |
| email | Required, valid email format, unique |
| contactNumber | Required, 9-20 chars, unique, allows +,-,(),spaces |
| dateOfBirth | Required, cannot be in future |
| skillIds | Optional, max 10 skills |

### Skill Fields
| Field | Rules |
|-------|-------|
| name | Required, 2-50 chars, unique, no leading/trailing spaces |

---

## Error Handling

API returns consistent error responses:

**400 Bad Request** - Validation error
```json
{
  "fullName": "Full name is required",
  "email": "Email is not valid"
}
```

**404 Not Found** - Resource doesn't exist
```json
{
  "error": "Candidate not found with id: 999"
}
```

**409 Conflict** - Duplicate resource
```json
{
  "error": "Candidate with email 'test@example.com' already exists"
}
```

---

### Entity Relationships
```
Candidate (1) ←→ (Many) Skills
├── id (PK)
├── fullName
├── email (UNIQUE)
├── contactNumber (UNIQUE)
├── dateOfBirth
└── skills (ManyToMany)

Skill (1) ←→ (Many) Candidates
├── id (PK)
└── name (UNIQUE)

candidate_skills (Join Table)
├── candidate_id (FK)
└── skill_id (FK)
```

### Validation Flow
```
HTTP Request
    ↓
@Validated + Jakarta Validation
    ↓
DTO validation (size, pattern, email, etc.)
    ↓
GlobalExceptionHandler
    ↓
Service business logic validation
    ↓
Repository save/update
    ↓
HTTP Response
```

---

## Configuration

### application.properties
- **Database:** PostgreSQL 16
- **JPA:** Hibernate 7.2.7
- **DDL:** Auto-update (update only, not for production)
- **SQL Logging:** Enabled in development

### Profiles
- `default` - Production settings
- `dev` - Development settings (with show-sql=true)
- `test` - Test settings (H2 in-memory)

---

## Troubleshooting

### Database Connection Failed
1. Ensure PostgreSQL is running: `psql -U postgres`
2. Check `.env` file values match your setup
3. For Docker: `docker-compose logs db`

### Port 8080 Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

### Docker Compose Connection Issues
```bash
# Rebuild containers and networks
docker-compose down -v
docker-compose up --build
```

---

## Production Considerations

⚠️ **Before deploying to production:**

1. **Security**
   - Change default database password
   - Use environment variables for secrets
   - Enable HTTPS/TLS

2. **Database**
   - Set `ddl-auto=validate` (not `update`)
   - Use database migrations (Flyway/Liquibase)
   - Regular backups

3. **Performance**
   - Set `show-sql=false`
   - Configure connection pooling
   - Add caching layer (Redis)

4. **Monitoring**
   - Add logging (ELK stack)
   - Enable metrics (Micrometer)
   - Set up health checks

---

## Development & Architecture

The most challenging part of this project was modeling the relationship between candidates and skills while ensuring data consistency. The solution uses a Many-to-Many JPA relationship with a dedicated join table, allowing flexible skill management without data duplication.

Key architectural decisions:
- **DTOs** for request/response separation and validation
- **Mappers** for entity ↔ DTO conversions
- **Service layer** for business logic isolation
- **Exception handling** for consistent error responses
- **Docker** for environment consistency

---

## License

MIT License - See LICENSE file for details

---
