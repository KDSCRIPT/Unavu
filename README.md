# Unavu Platform — Helm & Helmfile Setup Guide

This branch contains the Helm charts and declarative Helmfile for deploying the Unavu project in a Kubernetes cluster. 
Refer the main branch for the source code for kubernetes deployment and kubernetes server side load balancing.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Repository Structure](#repository-structure)
3. [Installing Required Tools](#installing-required-tools)
4. [Adding Helm Repositories](#adding-helm-repositories)
5. [Environment Configuration](#environment-configuration)
6. [Secrets Configuration](#secrets-configuration)
7. [Deploying the Platform](#deploying-the-platform)
8. [Verifying the Deployment](#verifying-the-deployment)
9. [Tearing Down](#tearing-down)
10. [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before starting, ensure you have the following:

- A running Kubernetes cluster (Minikube, Kind, EKS, GKE, etc.)
- `kubectl` configured and pointing to your cluster
- Internet access to pull Docker images and Helm charts
- Git (to clone this repository)

---

## Repository Structure

```
helm/
├── deploy/                          # Helmfile entrypoint
│   ├── helmfile.yaml                # Main helmfile
│   └── environments/                # Environment values
│       ├── prod.yaml                # Non-sensitive prod config
│       ├── dev.yaml                 # Non-sensitive dev config
│       ├── qa.yaml                  # Non-sensitive qa config
│       ├── secrets.prod.yaml        # ⚠ Sensitive prod secrets (gitignored)
│       ├── secrets.dev.yaml         # ⚠ Sensitive dev secrets (gitignored)
│       ├── secrets.qa.yaml          # ⚠ Sensitive qa secrets (gitignored)
│       └── secrets.example.yaml     # Template for secrets file
│
├── postgres/                        # PostgreSQL umbrella chart
├── kafka/                           # Kafka chart
├── rabbitmq/                        # RabbitMQ chart
├── keycloak/                        # Keycloak chart
├── kube-prometheus/                 # Prometheus chart
├── grafana-loki/                    # Loki chart
├── grafana-tempo/                   # Tempo chart
├── grafana-alloy/                   # Alloy chart
├── grafana/                         # Grafana chart
├── config-server/                   # Spring Cloud Config Server chart
├── spring-discovery/                # Spring Cloud Kubernetes Discovery Server
├── unavu-common/                    # Shared library chart (configmap + templates)
└── unavu-services/                  # Microservices
    ├── activity/
    ├── feed/
    ├── gateway-server/
    ├── list/
    ├── notification/
    ├── restaurant/
    ├── review/
    ├── social-graph/
    └── user/
```

---

## Installing Required Tools

### 1. Helm

```bash
# Windows (Chocolatey)
choco install kubernetes-helm -y

# macOS
brew install helm

# Linux
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash

# Verify
helm version
```

### 2. Helmfile

```bash
# Windows (Chocolatey)
choco install helmfile -y

# macOS
brew install helmfile

# Linux
curl -Lo helmfile https://github.com/helmfile/helmfile/releases/latest/download/helmfile_linux_amd64
chmod +x helmfile && sudo mv helmfile /usr/local/bin/

# Verify
helmfile version
```

### 3. helm-diff Plugin

Required by `helmfile apply`. Install after Helm:

```bash
# Remove any broken installation first
rm -rf ~/.helm/plugins/helm-diff  # Linux/macOS
# Windows (Git Bash):
rm -rf /c/Users/<USERNAME>/AppData/Roaming/helm/plugins/helm-diff

# Install
helm plugin install https://github.com/databus23/helm-diff --version 3.10.0 --verify=false

# Verify
helm plugin list
```

### 4. kubectl

```bash
# Windows
choco install kubernetes-cli -y

# macOS
brew install kubectl

# Linux
curl -LO https://dl.k8s.io/release/$(curl -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl
chmod +x kubectl && sudo mv kubectl /usr/local/bin/

# Verify
kubectl version --client
```

---

## Adding Helm Repositories

Run these commands before deploying:

```bash
helm repo add grafana https://grafana.github.io/helm-charts
helm repo add headlamp https://headlamp-k8s.github.io/headlamp
helm repo update
```

Verify repos are added:

```bash
helm repo list
```

---

## Environment Configuration

### 1. Clone the Repository

```bash
git clone -b helm <your-repo-url>
cd helm/deploy
```

### 2. Review `environments/prod.yaml`

This file contains all non-sensitive configuration. Review and update values as needed:

```yaml
global:
  configMapName: unavu-configmap
  activeProfile: prod
  profileActive: prod
  configServerURL: configserver:http://config-server:8071/
  discoveryServerURL: "http://spring-cloud-kubernetes-discoveryserver:80/"

  KeycloakServerUrl: http://keycloak:80
  KeycloakRealm: unavu
  KeycloakClientId: unavu-admin-client
  KeycloakIssuerUri: http://keycloak/realms/unavu
  KeycloakJWKSetUri: http://keycloak/realms/unavu/protocol/openid-connect/certs

  openTelemetryJavaAgent: "-javaagent:/app/libs/opentelemetry-javaagent-2.22.0.jar"
  otelExporterEndPoint: http://grafana-tempo.default.svc.cluster.local:4318
  otelMetricsExporter: none
  otelLogsExporter: none

  kafkaBrokerURL: kafka-controller-0.kafka-controller-headless.default.svc.cluster.local:9092
  KafkaHostname: kafka-controller-0.kafka-controller-headless.default.svc.cluster.local

  rabbitMQHost: rabbitmq
  rabbitMQUsername: guest

  springCloudConfigURI: http://config-server:8071/
  configRepoUserUrl: https://github.com/KDSCRIPT/Unavu.git
  configRepoUsername: KDSCRIPT
  readinessProbe:
    initialDelaySeconds: 60
    periodSeconds: 30

  livenessProbe:
    initialDelaySeconds: 60
    periodSeconds: 30

instances:
  - name: activity-db
    database: activity-db
    username: postgres
    port: 5432
    storage: 10Gi

  - name: feed-db
    database: feed-db
    username: postgres
    port: 5432
    storage: 10Gi

  - name: lists-db
    database: lists-db
    username: postgres
    port: 5432
    storage: 10Gi

  - name: notifications-db
    database: notifications-db
    username: postgres
    port: 5432
    storage: 10Gi

  - name: restaurants-db
    database: restaurants-db
    username: postgres
    port: 5432
    storage: 10Gi

  - name: reviews-db
    database: reviews-db
    username: postgres
    port: 5432
    storage: 10Gi

  - name: social-graph-db
    database: social-graph-db
    username: postgres
    port: 5432
    storage: 10Gi

  - name: users-db
    database: users-db
    username: postgres
    port: 5432
    storage: 10Gi
```

---

## Secrets Configuration

Secrets are **never committed to git**. You must create the secrets file manually.

### 1. Copy the Example File

```bash
cp environments/template.secrets.prod.yaml environments/secrets.prod.yaml
```

### 2. Fill in Your Secrets

Edit `environments/secrets.prod.yaml`:

```yaml
global:
  # Keycloak
  KeycloakClientSecret: "your-keycloak-client-secret"

  # RabbitMQ
  rabbitMQPassword: "your-rabbitmq-password"

  # GitHub token for Config Server (needs repo read access)
  configRepoPassword: "ghp_your_github_personal_access_token"

  # Spring Cloud Config encryption key
  encryptionKey: "your-encryption-key"

  # Mailtrap (email testing)
  MailtrapUsername: "your-mailtrap-username"
  MailtrapPassword: "your-mailtrap-password"
```

### 3. Generating a GitHub Personal Access Token

1. Go to GitHub → Settings → Developer Settings → Personal Access Tokens → Tokens (classic)
2. Click **Generate new token**
3. Select scope: `repo` (full repo access)
4. Copy the token and paste it as `configRepoPassword`

---

## Deploying the Platform

### 1. Update Chart Dependencies

Run this once before the first deploy and whenever `Chart.yaml` changes:

```bash
# Update all service chart dependencies
for dir in ../unavu-services/*/; do
  echo "Updating $dir"
  helm dependency update "$dir"
done

# Update infrastructure charts
helm dependency update ../grafana-loki
helm dependency update ../grafana-tempo
helm dependency update ../grafana-alloy
helm dependency update ../grafana
```

### 2. Deploy

```bash
cd deploy/
helmfile -e prod sync
```

This deploys all releases in the correct order:

```
postgres
├── kafka
├── rabbitmq
└── keycloak

kube-prometheus
├── grafana-loki
│   └── grafana-alloy
│   └── grafana
└── grafana-tempo
    └── grafana-alloy
    └── grafana

unavu-common (configmap + shared templates)
└── config-server
    └── spring-discovery
        ├── activity
        ├── feed
        ├── list
        ├── notification
        ├── restaurant
        ├── review
        ├── social-graph
        └── user
            └── gateway-server (starts last)
```

### 3. Deploy to Other Environments

```bash
# Development
helmfile -e dev sync

# QA
helmfile -e qa sync
```

> Make sure you have created `environments/secrets.dev.yaml` and `environments/secrets.qa.yaml` with the appropriate values.

---

## Verifying the Deployment

### Check All Pods Are Running

```bash
kubectl get pods -n default
```

All pods should show `Running` or `Completed`. Expected pods:

```
activity-*
config-server-*
feed-*
gateway-server-*
grafana-*
grafana-alloy-*
kafka-controller-*
keycloak-*
list-*
loki-*
notification-*
postgres-* (one per DB instance)
rabbitmq-*
restaurant-*
review-*
social-graph-*
spring-cloud-kubernetes-discoveryserver-*
tempo-*
user-*
```

### Check Services

```bash
kubectl get svc -n default
```

### Check Logs for a Specific Service

```bash
kubectl logs deployment/user -n default --tail=50
kubectl logs deployment/config-server -n default --tail=50
kubectl logs deployment/gateway-server -n default --tail=50
```

### Check Previous Crash Logs

```bash
kubectl logs <pod-name> -n default --previous
```

### Describe a Pod (for CreateContainerConfigError)

```bash
kubectl describe pod <pod-name> -n default
```

### Check Helm Release Values

```bash
helm get values postgres -n default
helm get values config-server -n default
```

### Check Helm Release Status

```bash
helm list -n default
```

---

## Tearing Down

### Destroy All Releases

```bash
helmfile -e prod destroy
```

### Destroy a Specific Release

```bash
helmfile -e prod destroy --selector name=gateway-server
```

### Uninstall a Single Helm Release Manually

```bash
helm uninstall <release-name> -n default
```

---

## Troubleshooting

### Chart.lock Out of Sync

```
Error: the lock file (Chart.lock) is out of sync with the dependencies file (Chart.yaml)
```

```bash
helm dependency update ../<chart-name>

# If chart has no dependencies, delete lock file:
rm -f ../<chart-name>/Chart.lock
```

### nil pointer evaluating interface{}.configMapName

Global values are not reaching the release. Ensure the release has a `values` block in `helmfile.yaml`:

```yaml
- name: my-service
  values:
    - ../environments/{{ .Environment.Name }}.yaml
    - ../environments/secrets.{{ .Environment.Name }}.yaml
```

### CreateContainerConfigError

The pod cannot find a referenced ConfigMap or Secret:

```bash
# Check what configmaps exist:
kubectl get configmap -n default

# Describe the pod for details:
kubectl describe pod <pod-name> -n default
```

Make sure `unavu-common` is deployed before all microservices.

### Config Server CrashLoopBackOff

Config server cannot clone the git repository:

```bash
kubectl logs deployment/config-server -n default --previous
```

Check:
1. `configRepoPassword` in `secrets.prod.yaml` is a valid GitHub token
2. `configRepoUserUrl` in `prod.yaml` points to the correct repo
3. The GitHub token has `repo` scope

### Connection Refused Between Services

```bash
# Check if target service pod is running:
kubectl get pods -n default | grep <service-name>

# Check if service exists:
kubectl get svc -n default | grep <service-name>

# Check env vars in pod:
kubectl exec -it <pod-name> -n default -- env | grep <VAR_NAME>
```

### helm-diff --validate Flag Error

```
Error: if any flags in the group [validate dry-run] are set none of the others can be set
```

Use `sync` instead of `apply`:

```bash
helmfile -e prod sync
```

---
