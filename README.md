# Unavu — Social Restaurant Catalog Platform

> Like Letterboxd, but for restaurants.

Unavu (உணவு — *meal* in Tamil) is a social restaurant discovery platform where users can review and rate restaurants, follow friends to see what they like, and create curated lists of their favourite places. Built as a long-term personal project to experiment with microservices, system design patterns, and DevOps tooling in a real-world context.

**Backend complete. Frontend (Angular) in progress.**

> This is the `main` branch — the Kubernetes version of the platform, deployed via Helm and Helmfile.
> For the Docker Compose version, see the `pre-kubernetes-docker-compose` branch.
> Helm charts live in the `helm` branch. Configuration files live in the `config` branch.

---

## What is Unavu?

- **Discover** restaurants and read reviews from people you follow
- **Review & Rate** your favourite restaurants
- **Create Lists** — public or private — to share your preferences with followers
- **Follow Friends** to see their activity, reviews and lists in your personalised feed
- **Get Notified** when people you follow post reviews or create lists
- **Track your Activity** with a personal journal of everything you do on the platform

---
## Key Migration: Eureka → Spring Cloud Kubernetes

The most significant architectural change from the Docker Compose version is the migration from **client-side load balancing (Eureka)** to **server-side load balancing (Spring Cloud Kubernetes)**.

| | Docker Compose branch | Main branch (Kubernetes) |
|---|---|---|
| Service Discovery | Netflix Eureka | Spring Cloud Kubernetes Discovery Server |
| Load Balancing | Client-side (Ribbon/Eureka) | Server-side (Kubernetes Services) |
| Service Registry | Eureka Server | Kubernetes native |
| Config | Spring Cloud Config + Eureka | Spring Cloud Config + K8s Discovery |

Services no longer register themselves with Eureka — Kubernetes handles service discovery natively via DNS and the Spring Cloud Kubernetes Discovery Server.

---

## Repository Branches

| Branch | Purpose |
|---|---|
| `main` | Spring Boot microservice source code — Kubernetes version |
| `helm` | All Helm charts and Helmfile for deploying to Kubernetes |
| `config` | Spring Cloud Config repository — environment-specific YAML configs |
| `pre-kubernetes-docker-compose` | Docker Compose version of the platform |

---

## Services

### Infrastructure

| Service | Description |
|---|---|
| **Config Server** | Spring Cloud Config Server — fetches config from the `config` branch on GitHub. Serves environment-specific YAML to all microservices on startup |
| **Spring Discovery Server** | Spring Cloud Kubernetes Discovery Server — replaces Eureka for server-side service discovery |
| **Gateway Server** | Single entry point. Rate limiting (Redis), retry, circuit breakers and timeouts |

### Domain Services

| Service | Port | Description |
|---|---|---|
| **User Service** | 8082 | Manages user profiles. Keycloak for OpenID Connect and OAuth2 |
| **Restaurant Service** | 8085 | Stores and manages restaurant data with search and filtering |
| **Review Service** | 8081 | Write and rate restaurants. Fans out to notification and feed |
| **List Service** | 8084 | Public or private curated restaurant lists |
| **Social Graph Service** | 8083 | Follow, block and mute relationships between users |

### Event-Driven Services

| Service | Port | Broker | Description |
|---|---|---|---|
| **Notification Service** | 9010 | RabbitMQ | In-app (SSE) and email (Mailtrap) notifications |
| **Feed Service** | 9011 | Kafka | Fan-out on write personalised feed |
| **Activity Service** | 9012 | Kafka | Personal activity journal |

### Observability Stack

| Tool | Description |
|---|---|
| **Grafana** | Unified dashboards for logs, metrics, traces and alerts |
| **Prometheus** (kube-prometheus) | Metrics from all services via Spring Actuator |
| **Loki** | Distributed log aggregation (read/write/backend split) |
| **Tempo** | Distributed tracing via OpenTelemetry Java Agent |
| **Grafana Alloy** | Log collection agent — scrapes pods and ships to Loki |
| **MinIO** | Object storage backend for Loki |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2, Spring Cloud |
| Auth | Keycloak 24.0 (OpenID Connect, OAuth2) — Helm chart |
| Service Discovery | Spring Cloud Kubernetes Discovery Server (server-side) |
| API Gateway | Spring Cloud Gateway |
| Async Messaging | RabbitMQ (notifications), Apache Kafka (feed, activity) |
| Database | H2 (dev), PostgreSQL 16 (qa, prod) — one DB per service |
| ORM | Spring Data JPA, Hibernate |
| HTTP Client | OpenFeign with Resilience4j |
| Rate Limiting | Redis |
| Observability | Loki + Prometheus + Tempo + Grafana + Alloy |
| Container Images | Jib Maven Plugin → Docker Hub (`containedtogether`) |
| Orchestration | Kubernetes (Docker Desktop local cluster) |
| Deployment | Helm + Helmfile |
| Config Management | Spring Cloud Config Server (backed by `config` branch) |

---

## Event Architecture

```
User posts a review
        │
        ├──► RabbitMQ ──► Notification Service ──► Notifies followers (SSE + email)
        │
        ├──► Kafka ──────► Feed Service ──────────► Fan-out to followers' feeds
        │
        └──► Kafka ──────► Activity Service ────────► Logs to actor's activity journal
```

---

## Database Strategy

| Environment | Database | Notes |
|---|---|---|
| `dev` | H2 in-memory | Zero setup, schema auto-created |
| `qa` | PostgreSQL 16 | One pod per service |
| `prod` | PostgreSQL 16 | One pod per service, persistent volumes (10Gi each) |

Each service has its own isolated database. Database hostnames match Kubernetes service names created by the PostgreSQL Helm chart (e.g. `activity-db:5432`, `reviews-db:5432`).

---

## Config Management

Configuration is managed by **Spring Cloud Config Server**, which clones the `config` branch on GitHub and serves environment-specific YAML files to each microservice on startup.

```
config branch (GitHub)
        ↓
Spring Cloud Config Server (Kubernetes pod)
        ↓
All microservices fetch config on startup
```

File naming convention:

| File | Loaded by |
|---|---|
| `application-prod.yaml` | All services (prod) — shared config |
| `review-prod.yaml` | Review service only (prod) |
| `activity-dev.yaml` | Activity service only (dev) |

Changes to the `config` branch take effect on the next service restart or via `/actuator/refresh`.

---

## Getting Started

### Prerequisites

- Docker Desktop with Kubernetes enabled
- `kubectl` configured and pointing to your local cluster
- `helm` installed
- `helmfile` installed
- `helm-diff` plugin installed

### Full Setup Guide

Deployment is managed entirely from the `helm` branch. Switch to it for step-by-step instructions:

```bash
git clone -b helm https://github.com/KDSCRIPT/Unavu.git
cd Unavu
```

The `helm` branch README covers:
- Installing Helm, Helmfile and helm-diff
- Adding required Helm repositories
- Secrets configuration
- Deploying with `helmfile -e prod sync`
- Verifying the deployment
- Troubleshooting common issues

### Quick Deploy (after tools are installed)

```bash
# Clone helm branch
git clone -b helm https://github.com/KDSCRIPT/Unavu.git
cd Unavu/helm/deploy

# Set up secrets
cp environments/template.secrets.prod.yaml environments/secrets.prod.yaml
# Edit secrets.prod.yaml with your values

# Update chart dependencies
for dir in ../unavu-services/*/; do
  helm dependency update "$dir"
done

# Deploy everything
helmfile -e prod sync
```

### Deployment Order (managed by Helmfile)

```
postgres, kafka, rabbitmq, keycloak     ← Infrastructure
kube-prometheus, loki, tempo, grafana   ← Observability
unavu-common (configmap)                ← Shared config
config-server                           ← Spring Cloud Config
spring-discovery                        ← K8s Discovery Server
All microservices                       ← Services
gateway-server                          ← Last (waits for all services)
```

---

## Building Docker Images

Images are built with **Jib Maven Plugin** and pushed to Docker Hub under `containedtogether`:

```bash
# Build and push all images
mvn compile jib:build

# Build a single service
cd restaurant
mvn compile jib:build
```

Images: `containedtogether/<service-name>`

---

## API Overview

All requests go through the gateway.

Full API docs via Swagger UI at each service: `http://localhost:<port>/swagger-ui.html`

---

## Observability

Grafana is available once the stack is deployed (anonymous access enabled — no login required).

| Dashboard | What it shows |
|---|---|
| Logs (Loki) | Searchable logs across all pods collected by Grafana Alloy |
| Metrics (Prometheus) | Request rates, error rates, JVM metrics via Spring Actuator |
| Traces (Tempo) | End-to-end distributed traces via OpenTelemetry Java Agent |
| Alerts (Grafana) | Pre-configured alerting rules |

Traces are collected automatically via the OpenTelemetry Java Agent (`opentelemetry-javaagent-2.22.0.jar`) attached to each service JVM and exported to Tempo.

---

## Keycloak Setup

Keycloak is deployed as a Helm chart. After `helmfile sync`:

1. Access the Keycloak admin console
2. Create a realm named `unavu`
3. Create a client named `unavu-admin-client`
4. Set the client secret to match `KeycloakClientSecret` in your `secrets.prod.yaml`
5. Configure Auth Code flow with PKCE for the Angular frontend (in progress)
6. Configure Client Credentials flow for service-to-service calls
---
## Repository

[https://github.com/KDSCRIPT/Unavu](https://github.com/KDSCRIPT/Unavu)

| Branch | Link |
|---|---|
| `main` (this branch) | Source code — Kubernetes version |
| `helm` | Helm charts and Helmfile |
| `config` | Spring Cloud Config files |
| `pre-kubernetes-docker-compose` | Docker Compose version |

Docker Hub: [containedtogether](https://hub.docker.com/u/containedtogether)

---

## License

MIT
