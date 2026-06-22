# vroomz-bidding-service AI Agent Instructions

This service implements bidding and auction logic for the Vroomz system.
It is a Spring Boot service registered with Eureka.

## Build & run
- Build: `./mvnw clean package`
- Test: `./mvnw test`
- Run: `./mvnw spring-boot:run`

## Key behavior
- Exposes bidding APIs under `/api/bids/**` (gateway route prefix).
- Uses Spring Data JPA with a MySQL runtime dependency.
- Registers with Eureka as `BIDDING-SERVICE`.

## Important conventions
- Preserve service boundaries between bidding logic and vehicle/user domains.
- Use the service root `HELP.md` for Maven and Spring references.
- Avoid assuming shared framework versions with the gateway or discovery service.

## Versions
- Spring Boot: `4.0.3`
- Spring Cloud: `2025.1.0`
