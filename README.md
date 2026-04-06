# HR Candidate Platform

This project is a simple HR system for adding, updating, deleting, and searching job candidates and their skills.

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Validation
- PostgreSQL
- Maven
- Lombok
- SpringDoc OpenAPI / Swagger UI
- Docker and Docker Compose
- JUnit 5 + Mockito for testing

## How to Run the Application

### 1. Running Locally from IDE

Make sure PostgreSQL is running and set the environment variables in `application.properties` or through a `.env` file:

```bash
DB_URL=jdbc:postgresql://localhost:5432/hr_platform
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

Then run the application from your IDE or via Maven wrapper:

```bash
./mvnw clean package -DskipTests && java -jar target/hr-platform-0.0.1-SNAPSHOT.jar
```

Or if you prefer to use the Spring Boot plugin directly:

```bash
./mvnw clean spring-boot:run
```

The application will start on `http://localhost:8080`.

### 2. Running with Docker

To start the entire stack with PostgreSQL:

```bash
docker compose up --build
```

The application will be available at:

- `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### 3. Running Tests

```bash
./mvnw test
```

## Most Interesting and Challenging Part

The most challenging part of this project was modeling the relationship between candidates and skills, and ensuring consistency across the REST API and database layer. It was crucial that a candidate could be created and updated independently of skills, while still allowing skills to be added or removed later without violating data integrity.

The decision to use a Many-to-Many relationship with a dedicated join table was natural because a candidate can have multiple skills, and a skill can be associated with multiple candidates. I also ensured that deleting a skill wouldn't leave orphaned foreign key references in the database by first removing all associations with candidates before deletion. This kept the solution simple and intuitive while being robust enough for a real-world CRUD scenario.

Another key decision was to keep the database and Docker configuration flexible, allowing the application to run both locally and in containers without code changes. This simplified development, testing, and eventual deployment of the entire stack via Docker Compose.
