# Vroomz AI Agent Instructions

This repository is part of a multi-service Spring Boot microservices system for Vroomz.
The workspace contains separate Maven-based services that are run independently and communicate through Eureka discovery and the Gateway.

## Services
- `discovery` — Eureka discovery server. Starts on port `8761` and should be launched first.
- `vroomz-gateway` — API gateway using Spring Cloud Gateway plus a custom JWT authentication filter.
- `vroomz-user-service` — user authentication / KYC service. Uses Spring Security and JWT.
- `vroomz-bidding-service` — bidding and auction logic service.
- `vroomz` — core vehicle service with Spring Data JPA and MySQL persistence.

## Build & run
- Build any service from its folder:
  - `./mvnw clean package`
  - `./mvnw test`
- Run a service:
  - `./mvnw spring-boot:run`

Recommended startup order:
1. `cd discovery && ./mvnw spring-boot:run`
2. `cd vroomz && ./mvnw spring-boot:run`
3. `cd vroomz-user-service && ./mvnw spring-boot:run`
4. `cd vroomz-bidding-service && ./mvnw spring-boot:run`
5. `cd vroomz-gateway && ./mvnw spring-boot:run`

## Important implementation details
- The gateway routes:
  - `/api/auth/**` → `USER-SERVICE`
  - `/api/vehicles/**` → `VROOMZ-SERVICE`
  - `/api/bids/**` → `BIDDING-SERVICE`
- The gateway includes a custom `AuthenticationFilter` that expects a Bearer JWT and enforces `ROLE_ADMIN` for `/api/vehicles/add`.
- The `vroomz` service uses a local MySQL database by default:
  - URL: `jdbc:mysql://localhost:3306/vroomz_db`
  - `spring.jpa.hibernate.ddl-auto=update`
- The services are not all on the same Spring Boot/Spring Cloud version:
  - `vroomz-gateway` and `discovery` use Spring Boot `3.2.3` / Spring Cloud `2023.0.0`
  - `vroomz`, `vroomz-user-service`, and `vroomz-bidding-service` use Spring Boot `4.0.3` / Spring Cloud `2025.1.0`
  - Avoid editing code with the assumption that all services share the same framework version.

## When updating code
- Preserve existing service boundaries and Eureka registration.
- Update gateway route definitions in `vroomz-gateway/src/main/resources/application.yml` if you change API paths or service names.
- Keep JWT secret configuration and routing logic separate from business-service logic.
- Use `HELP.md` in each service root for external Spring and Maven reference links.

## Quick facts for agents
- This is a Maven + Spring Boot microservice workspace.
- Use service-specific root folders rather than treating the whole workspace as a single Maven module.
- The main runtime integration relies on Eureka and gateway routing.
- No existing `AGENTS.md` or `copilot-instructions.md` file was present before this addition.
