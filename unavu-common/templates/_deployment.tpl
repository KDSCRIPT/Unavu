{{- define "common.deployment" -}}
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ .Values.deploymentName }}
  labels:
    app: {{ .Values.appLabel }}

spec:
  replicas: {{ .Values.replicaCount | default 1 }}

  selector:
    matchLabels:
      app: {{ .Values.appLabel }}

  template:
    metadata:
      labels:
        app: {{ .Values.appLabel }}

    spec:
      containers:
        - name: {{ .Values.appLabel }}
          image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
          imagePullPolicy: IfNotPresent
          startupProbe:
              httpGet:
                port: {{ .Values.containerPort }}
                path: /actuator/health/readiness
              failureThreshold: {{ .Values.startupProbe.failureThreshold }}
              periodSeconds: {{ .Values.startupProbe.periodSeconds }}
          readinessProbe:
              httpGet:
                port: {{ .Values.containerPort }}
                path: /actuator/health/readiness
              initialDelaySeconds: {{ .Values.readinessProbe.initialDelaySeconds }}
              periodSeconds: {{ .Values.readinessProbe.periodSeconds }}
          livenessProbe:
              httpGet:
                port: {{ .Values.containerPort }}
                path: /actuator/health/liveness
              initialDelaySeconds: {{ .Values.livenessProbe.initialDelaySeconds }}
              periodSeconds: {{ .Values.livenessProbe.periodSeconds }}
          ports:
            - containerPort: {{ .Values.containerPort }}
          envFrom:
            - configMapRef:
                name: {{ .Values.global.configMapName }}
          env:
            - name: SPRING_APPLICATION_NAME
              value: {{ .Values.appName | default .Values.appLabel }}
            - name: OTEL_SERVICE_NAME
              value: {{ .Values.appName | default .Values.appLabel }}

{{- end -}}