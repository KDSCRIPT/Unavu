# Unavu — Spring Cloud Config Repository

This branch serves as the configuration repository for the Unavu microservices platform. It is consumed by the **Spring Cloud Config Server** at runtime to provide environment-specific configuration to all services.

---

## How It Works

```
GitHub (this branch)
        ↓
Spring Cloud Config Server
        ↓
Microservices (fetch config on startup)
```

Each microservice fetches its configuration from the Config Server on startup using its `spring.application.name` and the active profile (`prod`, `dev`, `qa`). The Config Server clones this repository and serves the matching YAML files.

---

## Repository Structure

```
config/
├── prod/
│   ├── application-prod.yaml        # Shared config for all services (prod)
│   ├── activity-prod.yaml           # activity service config (prod)
│   ├── feed-prod.yaml               # feed service config (prod)
│   ├── list-prod.yaml               # list service config (prod)
│   ├── notification-prod.yaml       # notification service config (prod)
│   ├── restaurant-prod.yaml         # restaurant service config (prod)
│   ├── review-prod.yaml             # review service config (prod)
│   ├── social-graph-prod.yaml       # social-graph service config (prod)
│   └── user-prod.yaml               # user service config (prod)
│
├── dev/
│   ├── application-dev.yaml         # Shared config for all services (dev)
│   ├── activity-dev.yaml
│   ├── feed-dev.yaml
│   └── ...
│
└── qa/
    ├── application-qa.yaml          # Shared config for all services (qa)
    ├── activity-qa.yaml
    ├── feed-qa.yaml
    └── ...
```

---

## File Naming Convention

Spring Cloud Config Server resolves config files using:

```
{application-name}-{profile}.yaml
```

| File | Loaded by | Profile |
|---|---|---|
| `application-prod.yaml` | All services | prod |
| `activity-prod.yaml` | activity service only | prod |
| `user-dev.yaml` | user service only | dev |

Files named `application-{profile}.yaml` are shared across **all** services for that profile. Service-specific files override shared values.

---

## Environment Differences

### Production (`prod`)

- Uses **PostgreSQL** — one dedicated database per service
- SQL logging disabled
- Actuator exposes `health` and `info` only
- Log level set to `WARN` (minimal noise)

```yaml
# application-prod.yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    username: postgres
    password: root
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
management:
  endpoints:
    web:
      exposure:
        include: health,info
logging:
  level:
    root: WARN
    com.unavu: INFO
```

### Development (`dev`)

- Uses **H2 in-memory database** — no external DB needed
- H2 console enabled at `/h2-console`
- SQL logging enabled for debugging
- Actuator exposes all endpoints
- Log level set to show all application logs

```yaml
# application-dev.yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password: ""
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    show-sql: true
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

---

## Service Port Reference

| Service | Port |
|---|---|
| activity | 9012 |
| feed | 9011 |
| list | 8084 |
| notification | 9010 |
| restaurant | 8085 |
| review | 8081 |
| social-graph | 8083 |
| user | 8082 |

---

## Database Reference (Production)

| Service | Database Host | Database Name |
|---|---|---|
| activity | `activity-db:5432` | `activity-db` |
| feed | `feed-db:5432` | `feed-db` |
| list | `lists-db:5432` | `lists-db` |
| notification | `notifications-db:5432` | `notifications-db` |
| restaurant | `restaurants-db:5432` | `restaurants-db` |
| review | `reviews-db:5432` | `reviews-db` |
| social-graph | `social-graph-db:5432` | `social-graph-db` |
| user | `users-db:5432` | `users-db` |

> Database hostnames match the Kubernetes service names created by the PostgreSQL Helm chart.

---

## Adding a New Service

1. Create the service-specific config file:
```
prod/<service-name>-prod.yaml
dev/<service-name>-dev.yaml
qa/<service-name>-qa.yaml
```

2. Add the datasource URL and port at minimum:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://<service-name>-db:5432/<service-name>-db
server:
  port: <port>
```

3. Add a new PostgreSQL instance in `environments/prod.yaml` in the helm branch:
```yaml
instances:
  - name: <service-name>-db
    database: <service-name>-db
    username: postgres
    port: 5432
    storage: 10Gi
```

4. Commit and push — the Config Server picks up changes automatically on next service restart.

---

## Notes

- This branch is **read-only at runtime** — the Config Server clones it and serves files directly
- Sensitive values like passwords and secrets should ideally be **encrypted** using Spring Cloud Config's encryption support (`ENCRYPTION_KEY` env var on the Config Server)
- Changes to this branch take effect on the **next service restart** or when `/actuator/refresh` is called on a service
- The Config Server is configured to point to this branch via `configRepoUserUrl` and `configRepoPassword` in the Helm secrets file
