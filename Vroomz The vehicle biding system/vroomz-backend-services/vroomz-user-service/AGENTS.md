# vroomz-user-service AI Agent Instructions

This service handles user authentication, KYC, and JWT issuance for the Vroomz system.
It is a Spring Boot service registered with Eureka.

## Build & run
- Build: `./mvnw clean package`
- Test: `./mvnw test`
- Run: `./mvnw spring-boot:run`

## Key behavior
- Uses Spring Security and JWT via `io.jsonwebtoken`.
- Exposes auth endpoints under `/api/auth/**` (gateway route prefix).
- Uses MySQL at runtime with `mysql-connector-j` as a runtime dependency.
- Registers with Eureka as `USER-SERVICE`.

## Important conventions
- Keep authentication and user/KYC logic separated from service discovery config.
- Do not assume the same Spring Boot/Spring Cloud versions as the gateway or discovery service.
- Use `HELP.md` for external Spring Security and Eureka references when needed.

## Versions
- Spring Boot: `4.0.3`
- Spring Cloud: `2025.1.0`
