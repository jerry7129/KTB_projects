{{/*
Chart name
*/}}
{{- define "ktb-board-express.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Fully qualified application name
*/}}
{{- define "ktb-board-express.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Chart label
*/}}
{{- define "ktb-board-express.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "ktb-board-express.selectorLabels" -}}
app.kubernetes.io/name: {{ include "ktb-board-express.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Pod에 적용하는 라벨
*/}}
{{- define "ktb-board-express.podLabels" -}}
{{ include "ktb-board-express.selectorLabels" . }}
{{- with .Values.environment }}
environment: {{ . | quote }}
{{- end }}
{{- end }}


{{/*
리소스 metadata.labels 공통 라벨
*/}}
{{- define "ktb-board-express.labels" -}}
helm.sh/chart: {{ include "ktb-board-express.chart" . }}
{{ include "ktb-board-express.selectorLabels" . }}
{{- with .Chart.AppVersion }}
app.kubernetes.io/version: {{ . | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: ktb-board
{{- with .Values.environment }}
environment: {{ . | quote }}
{{- end }}
{{- end }}

{{/*
Deployment name
*/}}
{{- define "ktb-board-express.deploymentName" -}}
{{- printf "%s-deployment" (include "ktb-board-express.fullname" .) | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Service name
*/}}
{{- define "ktb-board-express.serviceName" -}}
{{- printf "%s-service" (include "ktb-board-express.fullname" .) | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
ConfigMap name
*/}}
{{- define "ktb-board-express.configMapName" -}}
{{- printf "%s-config" (include "ktb-board-express.fullname" .) | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
ServiceAccount name
*/}}
{{- define "ktb-board-express.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default
      (printf "%s-sa" (include "ktb-board-express.fullname" .))
      .Values.serviceAccount.name
      | trunc 63
      | trimSuffix "-"
-}}
{{- else }}
{{- required
      "serviceAccount.create=false이면 serviceAccount.name을 지정해야 합니다."
      .Values.serviceAccount.name
-}}
{{- end }}
{{- end }}

{{/*
HPA name
*/}}
{{- define "ktb-board-express.hpaName" -}}
{{- default
      (printf "%s-hpa" (include "ktb-board-express.fullname" .))
      .Values.autoscaling.name
      | trunc 63
      | trimSuffix "-"
-}}
{{- end }}

{{/*
HTTPRoute name
*/}}
{{- define "ktb-board-express.httpRouteName" -}}
{{- default
      (printf "%s-route" (include "ktb-board-express.fullname" .))
      .Values.httpRoute.name
      | trunc 63
      | trimSuffix "-"
-}}
{{- end }}

{{/*
TargetGroupConfiguration name
*/}}
{{- define "ktb-board-express.targetGroupConfigurationName" -}}
{{- default
      (printf "%s-target-group-config" (include "ktb-board-express.fullname" .))
      .Values.targetGroup.name
      | trunc 63
      | trimSuffix "-"
-}}
{{- end }}
