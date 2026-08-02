# CI/CD Host Setup — Unavu

This document describes the **one-time setup** required on any machine that will run the
`unavu-main-workflow` self-hosted GitHub Actions runner. Following these steps exactly means
the pipeline behaves identically on any host — nothing in the workflow files depends on a
personal login, a personal home directory, or machine-specific state.

The pipeline runs almost everything inside job containers. The host itself only needs:
Docker Engine, a dedicated service account, a couple of shared cache directories, and the
runner binary itself.

---

## 1. Install Docker Engine

```bash
curl -fsSL https://get.docker.com | sh
sudo systemctl enable --now docker
```

Confirm it's running:
```bash
docker version
```

---

## 2. Create the dedicated CI service account

The runner and all job containers run as a fixed, dedicated user — **not** your personal
login — so the pipeline doesn't depend on anyone's individual account existing on the box.

```bash
sudo useradd -m -u 1500 -s /usr/sbin/nologin ci-runner
```

- `-u 1500` — fixed UID, referenced directly in workflow files as `--user 1500:1500`
- `-s /usr/sbin/nologin` — this account never needs an interactive shell

Add it to the `docker` group so it can talk to the Docker daemon:
```bash
sudo usermod -aG docker ci-runner
```

---

## 3. Pin the `docker` group to a fixed, known GID

Workflow files reference the docker socket's group via a hardcoded `--group-add`, so this
GID needs to be consistent across any host running the pipeline.

```bash
getent group docker
```

If the GID isn't already `1001`, set it explicitly (pick any free number if `1001` is taken,
and use that same number everywhere below instead):
```bash
sudo groupmod -g 1001 docker
sudo usermod -aG docker ci-runner
```

---

## 4. Create the shared CI cache directory

All containerized jobs mount this single directory as `$HOME`, so Maven (`.m2`), SonarQube
scanner (`.sonar`), Jib (`.config/google-cloud-tools-java`), and Helm (`.cache/helm`,
`.config/helm`, `.local/share/helm`) all persist here across runs and across jobs.

```bash
sudo mkdir -p /opt/ci-cache/.m2/repository
sudo chown -R ci-runner:ci-runner /opt/ci-cache
```

---

## 5. Install the GitHub Actions runner, owned by `ci-runner`, under `/opt`

Do **not** install this under `/home/<your-user>` — a runner living inside a personal home
directory can hit directory-traversal permission errors for any other user (including
`ci-runner`) trying to execute it. `/opt` is a neutral, shared location any service account
can reach.

```bash
sudo mkdir -p /opt/actions-runner-unavu
sudo chown -R ci-runner:ci-runner /opt/actions-runner-unavu
cd /opt/actions-runner-unavu
```

Get the exact download command from your repo:
**Settings → Actions → Runners → New self-hosted runner** (version/checksum changes over
time, so always copy fresh from the UI rather than reusing an old command):

```bash
sudo -u ci-runner curl -o actions-runner-linux-x64-<version>.tar.gz -L \
  https://github.com/actions/runner/releases/download/v<version>/actions-runner-linux-x64-<version>.tar.gz
sudo -u ci-runner tar xzf ./actions-runner-linux-x64-<version>.tar.gz
```

---

## 6. Register the runner

Copy the exact `config.sh` command + token from the same GitHub UI page
(**Settings → Actions → Runners → New self-hosted runner**) — the token is short-lived:

```bash
sudo -u ci-runner ./config.sh --url https://github.com/KDSCRIPT/Unavu --token <TOKEN_FROM_UI>
```

Accept the defaults for runner name / labels (`self-hosted` is enough, since the workflows
just use `runs-on: self-hosted`) / work folder unless you have a reason to change them.

---

## 7. Test in the foreground, then install as a systemd service

```bash
sudo -u ci-runner ./run.sh
```
Confirm it prints **"Listening for Jobs"**, then `Ctrl+C`.

Install as a service running as `ci-runner` (so it survives reboots and doesn't depend on a
terminal session):
```bash
sudo ./svc.sh install ci-runner
sudo ./svc.sh start
sudo ./svc.sh status
```

Verify the real process is owned by `ci-runner`, not root:
```bash
ps -eo user,pid,cmd | grep -i runner
```

Verify on GitHub's side too: **Settings → Actions → Runners** should show the runner as
**Idle** (green).

---

## 8. Configure repository secrets

None of the following live on the host — they're injected per-job from GitHub Secrets, so a
freshly set-up host needs zero manual credential files.

| Secret | Purpose | Format |
|---|---|---|
| `DEV_SECRETS_YAML` / `QA_SECRETS_YAML` / `PROD_SECRETS_YAML` | Environment-specific Helm values (DB passwords, API keys, etc.) | Plain YAML text |
| `DEV_KUBECONFIG` / `QA_KUBECONFIG` / `PROD_KUBECONFIG` | Cluster credentials for each environment | Base64-encoded kubeconfig |
| `DOCKERHUB_TOKEN` | Docker Hub push/pull access | Token string |
| `SONARQUBE_TOKEN` | SonarQube analysis auth | Token string |
| `NVD_API_KEY` | OWASP Dependency-Check NVD API access | Token string |

**Set these as plain repository secrets, not GitHub Environment-scoped secrets**, unless you
explicitly add a matching `environment:` key to the job that needs them — otherwise the job
simply won't see the secret at all (this bit us with `PROD_SECRETS_YAML` once already).

Generating the kubeconfig secrets:
```bash
cat ~/.kube/config | base64 -w 0 | gh secret set DEV_KUBECONFIG --repo KDSCRIPT/Unavu
```
Piping directly via `gh secret set` avoids clipboard/browser paste corruption — don't
copy-paste the base64 string through the web UI if you can avoid it.

Generating the plain-YAML secrets:
```bash
cat environments/secrets.prod.yaml | gh secret set PROD_SECRETS_YAML --repo KDSCRIPT/Unavu
```
No base64 needed here — this one is consumed as raw text by the workflow.

---

## 9. Configure repository variables

| Variable | Purpose | Value |
|---|---|---|
| `SONARQUBE_URL` | SonarQube server address, reachable from inside job containers | `http://host.docker.internal:9000` |

Note this is **not** `http://localhost:9000` — job containers have their own network
namespace, so `localhost` inside a container means the container itself, not the host
running SonarQube. `host.docker.internal` (enabled via `--add-host=host.docker.internal:host-gateway`
in the SonarQube job's container options) is what actually reaches the host.

---

## 10. Docker socket permissions note

This setup grants `ci-runner` access to `/var/run/docker.sock` via group membership
(`--group-add 1001`), rather than loosening the socket's permissions globally
(`SocketMode=0666`). If you ever change this to a shared/production machine, keep it this
way — a world-writable Docker socket gives any container full root-equivalent control over
the host, which is fine for a personal single-developer box but not appropriate elsewhere.

---

## 11. Custom CI images used by the pipeline

Two custom images are built once and pushed to Docker Hub, so the pipeline doesn't need to
install packages at runtime (which fails under a non-root container user anyway):

| Image | Purpose | Dockerfile |
|---|---|---|
| `containedtogether/maven-docker-ci:21` | Maven + Docker CLI, for Jib's `dockerBuild` goal | `ci/maven-docker-ci.Dockerfile` |
| `containedtogether/docker-curl-jq:latest` | Docker CLI + curl + jq + GNU grep, for the promote-to-prod job | `ci/docker-curl-jq.Dockerfile` |

The pipeline's `Ensure_CI_Image` job checks whether these already exist on Docker Hub and
builds/pushes them automatically if not — so this step is self-healing and doesn't require
manual intervention on a new host, beyond having `DOCKERHUB_TOKEN` configured.

---

## Summary — what a brand new host actually needs

1. Docker Engine, running
2. `ci-runner` user (UID 1500), in the `docker` group (GID 1001)
3. `/opt/ci-cache` owned by `ci-runner`
4. Runner registered and running as a systemd service under `/opt/actions-runner-unavu`
5. Secrets and variables configured in the GitHub repo (not on the host)

No JDK, Maven, Helm, Helmfile, kubectl, Trivy, jq, or curl need to be installed on the host
directly — all of that lives inside the job container images referenced in the workflow
files.