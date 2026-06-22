# vroomz-gateway AI Agent Instructions

This service is the API gateway for the Vroomz microservice system.
It uses Spring Cloud Gateway and routes requests to downstream services through Eureka discovery.

## Build & run
- Build: `./mvnw clean package`
- Test: `./mvnw test`
- Run: `./mvnw spring-boot:run`

## Key behavior
- Routes defined in `src/main/resources/application.yml`.
- JWT authentication is enforced by `com.vroomz.gateway.filter.AuthenticationFilter`.
- The gateway expects a `jwt.secret` configuration value and defaults to `vroomz_super_secret_key_for_shriram_transport_project_2026` when `JWT_SECRET` is not set.
- Route mappings:
  - `/api/auth/**` → `lb://USER-SERVICE`
  - `/api/vehicles/**` → `lb://VROOMZ-SERVICE`
  - `/api/bids/**` → `lb://BIDDING-SERVICE`

## Important conventions
- Keep gateway route names and service IDs aligned with downstream application names.
- Preserve the authentication filter logic separately from route definitions.
- Avoid assuming shared Spring Boot/Spring Cloud versions across services.

## Versions
- Spring Boot: `3.2.3`
- Spring Cloud: `2023.0.0`
