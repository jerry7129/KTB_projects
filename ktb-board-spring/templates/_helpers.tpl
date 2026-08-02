{{/*
Expand the name of the chart.
*/}}
{{- define "ktb-board-spring.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "ktb-board-spring.fullname" -}}
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
Create chart name and version as used by the chart label.
*/}}
{{- define "ktb-board-spring.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "ktb-board-spring.labels" -}}
helm.sh/chart: {{ include "ktb-board-spring.chart" . }}
{{ include "ktb-board-spring.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "ktb-board-spring.selectorLabels" -}}
app.kubernetes.io/name: {{ include "ktb-board-spring.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Spring 애플리케이션 ServiceAccount 이름
*/}}
{{- define "ktb-board-spring.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default
      (printf "%s-sa" (include "ktb-board-spring.fullname" .))
      .Values.serviceAccount.name
-}}
{{- else }}
{{- required
      "serviceAccount.create=false이면 serviceAccount.name을 지정해야 합니다."
      .Values.serviceAccount.name
-}}
{{- end }}
{{- end }}

{{/*
Deployment 리소스 이름
*/}}
{{- define "ktb-board-spring.deploymentName" -}}
{{- printf "%s-deployment" (include "ktb-board-spring.fullname" .) | trunc 63 | trimSuffix "-" }}
{{- end }}
