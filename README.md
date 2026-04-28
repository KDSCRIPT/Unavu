# Unavu — Social Restaurant Catalog Platform

Unavu is a social catalog platform for restaurants. Discover restaurants, post reviews, follow friends to see what they like, and curate lists of your favourite places.

> This is the `pre-kubernetes-docker-compose` branch — the Docker Compose version of the platform.
> The project has since been migrated to Kubernetes with Helm and Helmfile. See the `main` branch for the latest.

---

## What is Unavu?

- **Discover** restaurants and read reviews from people you follow
- **Review & Rate** your favourite restaurants
- **Create Lists** — public or private — to share your preferences with followers
- **Follow Friends** to see their activity, reviews and lists in your feed
- **Get Notified** when people you follow post reviews or create lists
- **Track your Activity** with a personal journal of everything you do on the platform

---
## Services

### Infrastructure

| Service | Port | Description |
|---|---|---|
| **Config Server** | 8071 | Spring Cloud Config Server — centralised configuration management across dev, qa and prod environments |
| **Eureka Server** | 8070 | Service discovery and client-side load balancing. Planned migration to server-side discovery |
| **Gateway Server** | 8072 | Single entry point for all client requests. Implements rate limiting (Redis), retry mechanism, circuit breakers and timeouts for resilience |

### Domain Services

| Service | Port | Description |
|---|---|---|
| **User Service** | 8082 | Manages user profiles. Integrates with Keycloak for authentication via OpenID Connect and OAuth2 |
| **Restaurant Service** | 8085 | Stores and manages restaurant data including search, filtering by cuisine, area and city |
| **Review Service** | 8081 | Write and rate restaurants. Reviews trigger notifications and feed events to followers |
| **List Service** | 8084 | Create public or private lists of restaurants. Followers can discover your preferences through your lists |
| **Social Graph Service** | 8083 | Manages follow, block and mute relationships between users. Powers the follower graph used by feed and notifications |

### Event-Driven Services

| Service | Port | Broker | Description |
|---|---|---|---|
| **Notification Service** | 9010 | RabbitMQ | Push-based notification system. Notifies followers of new reviews, lists and restaurants via in-app and email (SMTP) notifications |
| **Feed Service** | 9011 | Kafka | Fan-out on write feed system. Aggregates activity from followed users into a personalised feed |
| **Activity Service** | 9012 | Kafka | Tracks all user actions as a personal activity journal — reviews posted, lists created, users followed and more |

### Observability

| Tool | Description |
|---|---|
| **Loki** | Log aggregation |
| **Prometheus** | Metrics collection |
| **Tempo** | Distributed tracing |
| **Grafana** | Unified dashboards for logs, metrics and traces |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2, Spring Cloud |
| Auth | Keycloak (OpenID Connect, OAuth2) |
| Service Discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Async Messaging | RabbitMQ (notifications), Apache Kafka (feed, activity) |
| Database | H2 (dev), PostgreSQL (qa, prod) |
| ORM | Spring Data JPA, Hibernate |
| HTTP Client | OpenFeign with Resilience4j (circuit breaker, retry, timeout) |
| Observability | Loki + Prometheus + Tempo + Grafana |
| Containerisation | Docker Compose (migration to Kubernetes planned) |

---

## Event Architecture
- **RabbitMQ** handles notifications — push-based, delivery-guaranteed
- **Kafka** handles feed and activity — event-streamed, high-throughput, replay-capable

---

## Database Strategy

| Environment | Database |
|---|---|
| `dev` | H2 in-memory — zero setup, fast iteration |
| `qa` | PostgreSQL — mirrors production |
| `prod` | PostgreSQL — persistent, production-grade |

Profile is activated via `PROFILE_ACTIVE` environment variable.

---

## Getting Started

### Prerequisites

- Java 21
- Docker and Docker Compose
- Keycloak instance running
- RabbitMQ running on `localhost:5672`
- Kafka running on `localhost:9092`
- Redis running on `localhost:6379` (gateway rate limiting)

### Running with Docker Compose

```bash
# Clone the repository
git clone https://github.com/your-username/unavu.git
cd unavu

# Start all services
docker compose up -d

```

### Running Locally (Development)

Start services in this order:

```bash
# 1. Infrastructure first
config-server    # port 8071
eureka-server    # port 8070

# 2. Domain services
user-service     # port 8082
restaurant       # port 8085
review           # port 8081
list             # port 8084
social-graph     # port 8083

# 3. Event-driven services
notification     # port 9010
feed             # port 9011
activity         # port 9012

# 4. Gateway last
gateway-server   # port 8072
```

---

## API Overview

All requests go through the gateway at `http://localhost:8072`.

Full API documentation available via Swagger UI at each service's `/swagger-ui.html`.

---

## Observability

Grafana dashboards are available at `http://localhost:3000` once the observability stack is running via Docker Compose.

- **Logs** — search and filter logs across all services via Loki
- **Metrics** — service health, request rates, error rates via Prometheus
- **Traces** — end-to-end request tracing across microservices via Tempo

---

## License

MIT
