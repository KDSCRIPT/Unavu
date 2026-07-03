{{- define "common.configmap" -}}
apiVersion: v1
kind: ConfigMap
metadata:
  name: {{ .Values.global.configMapName }}

data:
  SPRING_PROFILES_ACTIVE: "{{ .Values.global.activeProfile }}"
  PROFILE_ACTIVE: "{{ .Values.global.profileActive }}"
  SPRING_CLOUD_CONFIG_URI: "{{ .Values.global.springCloudConfigURI }}"

  SPRING_CLOUD_KUBERNETES_DISCOVERY_DISCOVERY_SERVER_URL: "{{ .Values.global.discoveryServerURL }}"

  #EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: "{{ .Values.global.eurekaDefaultZone }}"
  
  KEYCLOAK_SERVER_URL: "{{ .Values.global.KeycloakServerUrl }}"
  KEYCLOAK_REALM: "{{ .Values.global.KeycloakRealm }}"
  KEYCLOAK_CLIENT_ID: "{{ .Values.global.KeycloakClientId }}"
  KEYCLOAK_CLIENT_SECRET: "{{ .Values.global.KeycloakClientSecret }}"
  JWK_SET_URI: "{{ .Values.global.KeycloakJWKSetUri }}"
  KEYCLOAK_ISSUER_URI: "{{ .Values.global.KeycloakIssuerUri }}"
  

  SPRING_CLOUD_STREAM_KAFKA_BINDER_BROKERS: "{{ .Values.global.kafkaBrokerURL }}"
  KAFKA_HOSTNAME: "{{ .Values.global.KafkaHostname }}"

  JAVA_TOOL_OPTIONS: "{{ .Values.global.openTelemetryJavaAgent }}"
  OTEL_EXPORTER_OTLP_ENDPOINT: "{{ .Values.global.otelExporterEndPoint }}"
  OTEL_METRICS_EXPORTER: "{{ .Values.global.otelMetricsExporter }}"
  OTEL_LOGS_EXPORTER: "{{ .Values.global.otelLogsExporter }}"

  MAILTRAP_USERNAME: "{{ .Values.global.MailtrapUsername }}"
  MAILTRAP_PASSWORD: "{{ .Values.global.MailtrapPassword }}"

{{- end -}}