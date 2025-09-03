# careercrush-backend

A Spring Boot backend for the CareerCrush / Stemlen project. This README explains how to set up the project locally, run it, and contribute.

## Checklist of what this README provides
- Prerequisites (Java, Maven, Docker)
- How to run locally (Maven wrapper and packaged jar)
- How to set configuration and secrets safely
- Docker build/run instructions
- Useful commands for contributors

## Prerequisites
- Java 17 (JDK 17)
- Git
- Internet access to download dependencies
- (Optional) Docker, if you prefer to run inside a container

Notes:
- This project uses the included Maven wrapper (`mvnw` / `mvnw.cmd`) so you do not need to have Maven preinstalled.

## Quick start — run locally

Open a terminal in the `careercrushbackend` directory.

On macOS / Linux:

```
./mvnw spring-boot:run
```

On Windows (PowerShell):

```
.\mvnw.cmd spring-boot:run
```

This will start the application on port 8080 by default. You can change the port using `--server.port` or via properties.

## Build a runnable JAR

Package the application:

On macOS / Linux:

```
./mvnw clean package -DskipTests
```

On Windows (PowerShell):

```
.\mvnw.cmd clean package -DskipTests
```

Then run the produced jar:

```
java -jar target/stemlen-0.0.1-SNAPSHOT.jar
```

Adjust the jar name to match the built artifact if it differs.

## Configuration and secrets

Configuration lives in `src/main/resources/application.properties`. The repository currently contains example properties, including placeholders for OAuth and mail configuration. Do NOT commit real secrets.

Preferred approaches for contributors:

1. Use environment variables (Spring Boot auto-maps environment variable names from property keys). Example environment variables to set:

- `SPRING_DATA_MONGODB_URI` — MongoDB connection URI
- `SPRING_MAIL_USERNAME` and `SPRING_MAIL_PASSWORD` — SMTP credentials
- `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID` and similar OAuth keys

2. Or create a local `application-local.properties` (ignored by git) and run with:

```
./mvnw spring-boot:run -Dspring.profiles.active=local
```

Example `.env`-style snippet (do not commit):

```
SPRING_DATA_MONGODB_URI=mongodb+srv://<user>:<password>@cluster.example.net/Database
SPRING_MAIL_USERNAME=your-email@example.com
SPRING_MAIL_PASSWORD=supersecret
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=...
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=...
```

On Windows PowerShell you can set environment vars for a single command like this:

```
$env:SPRING_DATA_MONGODB_URI = 'mongodb+srv://...'; .\mvnw.cmd spring-boot:run
```

Or pass them as JVM properties:

```
.\mvnw.cmd spring-boot:run -Dspring.data.mongodb.uri="mongodb+srv://..."
```

## Docker

Build image (from `careercrushbackend` directory):

```
docker build -t careercrush-backend .
```

Run with environment variables (example):

```
docker run -p 8080:8080 \
	-e SPRING_DATA_MONGODB_URI='mongodb+srv://<user>:<pw>@cluster...' \
	-e SPRING_MAIL_USERNAME='you@example.com' \
	-e SPRING_MAIL_PASSWORD='secret' \
	careercrush-backend
```

## Tests

Run unit tests:

On macOS / Linux:

```
./mvnw test
```

On Windows:

```
.\mvnw.cmd test
```

## Useful commands summary
- Run locally: `./mvnw spring-boot:run` or `.\mvnw.cmd spring-boot:run`
- Build: `./mvnw clean package` or `.\mvnw.cmd clean package`
- Run jar: `java -jar target/stemlen-0.0.1-SNAPSHOT.jar`
- Run tests: `./mvnw test` or `.\mvnw.cmd test`

## Where to find things
- Source: `src/main/java`
- Resources/config: `src/main/resources/application.properties`
- Tests: `src/test/java`
- Dockerfile: `Dockerfile` (project root)

## Contribution notes
- Please open an issue or a pull request. Follow existing code style.
- Do not commit sensitive credentials; use environment variables or local config files.

## Troubleshooting
- If the app fails to connect to MongoDB, double-check `SPRING_DATA_MONGODB_URI` and IP/network access for your DB.
- For SMTP issues, ensure username/password and `spring.mail.properties.mail.smtp.starttls.enable=true` if required by the mail provider.

## Requirements coverage
- Create a contributor README with install and run steps: Done

---
If you'd like, I can also add a sample `application-local.properties.example` or a `.env.example` file to the repo to make local setup even easier. 
