# discovery AI Agent Instructions

This repository is the Eureka discovery server for the Vroomz microservice system.
It must start before the other services so they can register successfully.

## Build & run
- Build: `./mvnw clean package`
- Test: `./mvnw test`
- Run: `./mvnw spring-boot:run`

## Key behavior
- Enables Eureka Server with `@EnableEurekaServer` in `VroomzDiscoveryServerApplication`.
- Runs on port `8761` by convention.
- Downstream services rely on `http://localhost:8761/eureka/` for service discovery.

## Important conventions
- Start this service first during local development.
- Do not change the discovery URL in downstream services without updating the gateway and service configs.

## Versions
- Spring Boot: `3.2.3`
- Spring Cloud: `2023.0.0`
