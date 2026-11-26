# Notificaciones planificadas

Estado: requisitos v5.0; FCM y WorkManager no están incorporados.

## Push remotas

FCM será transporte. FastAPI/PostgreSQL decide destinatarios y registra eventos en notification_outbox dentro de la transacción autorizada: TRAINER_REQUEST_RECEIVED, TRAINER_REQUEST_ACCEPTED, TRAINER_REQUEST_REJECTED y ROUTINE_ASSIGNED. Un procesador sencillo posterior al commit enviará mediante Firebase Admin SDK con reintentos/idempotencia; no garantiza entrega inmediata ni envía eventos revertidos. device_registrations asociará installation_id/token a usuario autenticado y evitará duplicados activos. PUT/DELETE /api/v1/devices/{installation_id} son contratos preliminares, sin implementación. No endpoint público de envío libre.

Android pedirá POST_NOTIFICATIONS desde API 33 en contexto y creará canales desde API 26. Permiso rechazado no impide usar FitTrack. Verificará identidad/permisos al recibir y abrir, no expondrá secretos ni contenido privado en pantalla bloqueada, no registrará tokens en logs y desvinculará instalación/notificaciones al logout, incluso ante desconexión con reconciliación posterior. Credenciales Firebase Admin solo en servidor; no Firebase Auth ni Firestore.

## Recordatorios locales

WorkManager programará un trabajo único por usuario para hábitos pendientes, con hora inicial propuesta 20:00 local configurable y opción de desactivar. El worker consultará DataStore y fecha antes de mostrar; no notificará si el día ya cumple la meta vigente de tres hábitos. Manejará zona horaria/fecha y duplicados, y cancelará trabajo al logout. No usar alarmas exactas ni prometer hora exacta.

Implementación, permisos, modelos y migraciones corresponden a etapas 17–19.
